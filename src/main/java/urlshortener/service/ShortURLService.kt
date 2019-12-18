package urlshortener.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.util.*
import javax.imageio.ImageIO
import khttp.post
import org.apache.commons.validator.routines.UrlValidator
import com.google.zxing.client.j2se.MatrixToImageWriter
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import redis.clients.jedis.Jedis
import urlshortener.domain.ShortURL
import urlshortener.exception.BadRequestError
import urlshortener.repository.ShortURLRepository

@Service
public class ShortURLService(private val shortURLRepository: ShortURLRepository) {

    private val REGEX_BRACKET = Regex("\\{[a-z]+\\}", setOf(RegexOption.IGNORE_CASE))
    private val REGEX_VANITY = Regex("[a-z]+\\/(\\{[a-z]+\\})", setOf(RegexOption.IGNORE_CASE))
    private val BANNED_VANITY = listOf("api", "swagger-ui")

    @Value("\${google.safebrowsing.api_key}")
    var safeBrowsingKey: String? = null

    // Necessary for self-invocation of cache function
    @Autowired
    private val shortURLService: ShortURLService? = null

    @Autowired
    private val env: Environment? = null

    public fun findByKey(id: String): Mono<ShortURL> = shortURLRepository.findByKey(id)

    public fun save(url: String, ip: String, vanity: String? = null): Mono<ShortURL> {
        // Valid URL
        val urlValidator = UrlValidator(arrayOf("http", "https"))
        if (!urlValidator.isValid(url)) {
            return Mono.error(BadRequestError("Invalid URL to short"))
        }
        // Valid vanity
        if (!vanity.isNullOrBlank() && vanity in BANNED_VANITY) {
            return Mono.error(BadRequestError("Vanity cannot start with " + vanity))
        }
        var su = ShortURLBuilder()
                .target(url, vanity)
                .createdNow()
                .temporaryRedirect()
                .ip(ip)
                .build()
        // Treat vanity for URL templates
        if (!vanity.isNullOrBlank()) {
            su = validateVanity(su).block()!!
        }
        // Save and start checking safe browsing
        checkSafeBrowsing(su)
        return shortURLRepository.save(su)
    }

    fun generateQR(qrCodeText: String, size: Int = 400): Mono<String>? {
        if (shortURLService != null) {
            return Mono.just(shortURLService.generateQRString(qrCodeText, size))
        } else {
            return null
        }
    }

    @Cacheable("qrs", key = "#qrCodeText")
    fun generateQRString(qrCodeText: String, size: Int = 400): String {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        val byteMatrix = QRCodeWriter().encode(qrCodeText, BarcodeFormat.QR_CODE, size, size)
        val baos = ByteArrayOutputStream()
        baos.use {
            MatrixToImageWriter.writeToStream(byteMatrix, "png", baos)
            baos.flush()
            return Base64.getEncoder().encodeToString(baos.toByteArray())
        }
    }

    public fun checkSafeBrowsing(su: ShortURL): Mono<ShortURL> =
        Mono.fromSupplier{ shortURLService!!.safeBrowsing(su) }.subscribeOn(Schedulers.elastic())

    @Cacheable("safeURLs", key = "#su.id", unless = "!#result.safe")
    fun safeBrowsing(su: ShortURL): ShortURL {
        simulateSlowService()
        su.safe = su.target?.let { isSafe(it) }
        return su
    }

    fun isSafe(url: String): Boolean {
        val mapClient = mapOf("clientId" to "es.unizar.urlshortener", "clientVersion" to "1.0.0")
        val mapThreatInfo = mapOf("threatTypes" to listOf("MALWARE", "SOCIAL_ENGINEERING"),
                "platformTypes" to listOf("WINDOWS"),
                "threatEntryTypes" to listOf("URL"),
                "threatEntries" to listOf(mapOf("url" to url)))
        // khttp 0.1.0 doesn't allow async petitions, and there are no upgrades available
        val r = post("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=$safeBrowsingKey",
                data = JSONObject(mapOf("client" to mapClient, "threatInfo" to mapThreatInfo)), timeout = 1.0)
        return JSONObject(r.text).length() == 0
    }

    private fun simulateSlowService() {
        try {
            // URL should not be accessible in the first 20 seconds
            // afters 20s execute safe check
            val time = 20000L
            Thread.sleep(time)
        } catch (e: InterruptedException) {
            throw IllegalStateException(e)
        }
    }

    public fun validateVanity(su: ShortURL): Mono<ShortURL> {
        val matchVanity = REGEX_BRACKET.find(su.id!!)?.groups
        val matchUrl = REGEX_BRACKET.find(su.target!!)?.groups
        if (matchVanity != null && matchUrl != null) {
            // Same length
            if (matchUrl.count() != 1 || matchUrl.count() != 1) {
                return Mono.error(BadRequestError("URL and vanity should only have one group"))
            }
            // Not repeated
            val stringVanity = matchVanity.get(0)!!.value
            val stringUrl = matchVanity.get(0)!!.value
            if (stringVanity != stringUrl) {
                return Mono.error(BadRequestError("Vanity groups don't match"))
            }
            // Matches are one to one (also modify target & hash)
            su.target = su.target!!.replace(stringUrl, "{0}")
            su.id = su.id!!.replace(stringVanity, "{0}")
        }
        return Mono.just(su)
    }

    @Scheduled(fixedRate = 600000) // 600 segundos
    fun reviewSafeURLs() {
        val jedis = Jedis(env?.getProperty("spring.redis.host"), 6379)
        for (cachedString in jedis.keys("safeURLs::*")) {
            val parts = cachedString.split(":", limit = 3)
            val cachedId = parts[2]
            val url = shortURLService?.obtainUrl(cachedId)
            if (url != null) {
                println("Checking if the url ${url.target} is safe")
                if (!url.target?.let { isSafe(it) }!!) {
                    url.target?.let { shortURLService?.removeUrl(it) }
                    println("The url $url.target is not safe")
                }
            }
        }
    }

    @Cacheable("safeURLs", key = "#id")
    fun obtainUrl(id: String): ShortURL {
        // TODO: create an error if URL does not exist in cache
        println("Error, the url $id is not saved in the cache")
        return ShortURL()
    }

    @CacheEvict("safeURLs", key = "#id")
    fun removeUrl(id: String) {
        // TODO: create an error if URL does not exist in cache
        println("Error, the url $id is not saved in the cache")
    }
}
