/*
 *******************************************
 *** Urlshortener - Web Engineering ********
 *** Authors: Name  ************************
 *** Andrew Mackay - 737069 ****************
 *** Ruben Rodríguez Esteban - 737215 ******
 *** Diego Royo Meneses - 740388 ***********
 *** Course: 2019 - 2020 *******************
 *******************************************
 */ 

package urlshortener.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import java.util.*
import khttp.post
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.apache.commons.validator.routines.UrlValidator
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import redis.clients.jedis.Jedis
import urlshortener.domain.ShortURL
import urlshortener.exception.BadRequestError
import urlshortener.repository.ShortURLRepository
import urlshortener.exception.ServiceUnavailableError
import java.time.Duration
import reactor.core.scheduler.Schedulers

@Service
class ShortURLService(private val shortURLRepository: ShortURLRepository) {

    // Regular expresion
    private val REGEX_BRACKET = Regex("\\{[a-z]+\\}", setOf(RegexOption.IGNORE_CASE))

    @Value("\${google.safebrowsing.api_key}")
    var safeBrowsingKey: String? = null

    @Autowired
    private val env: Environment? = null

    /**
     * Returns the url with <<id>> from the database.
     * @param id Id of the short url.
     * @return The url with <<id>> id from the repository.
     */
    fun findByKey(id: String): Mono<ShortURL> = shortURLRepository.findByKey(id)



     /**
     * Saves the url in the database.
     * @param url Url which is going to be stored.
     * @param vanity Vanity of the url.
     */
    fun save(url: String, ip: String, vanity: String? = null): Mono<ShortURL> {
        // Valid URL
        val urlValidator = UrlValidator(arrayOf("http", "https"))
        // Check if the url is valid 
        if (!urlValidator.isValid(url.replace("{", "").replace("}", ""))) {
            throw BadRequestError("Invalid URL to short")
        }
        // Check if the there is error in the vanity
        if (!vanity.isNullOrBlank() && (vanity.startsWith("api") || vanity == "swagger-ui")) {
            throw BadRequestError("Vanity cannot start with $vanity")
        }
        // Creattion of the url shortened
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
        GlobalScope.async {
            checkSafeBrowsing(su).block()!!
        }
        // Save the url shortened
        return shortURLRepository.save(su)
    }



    /**
     * @param qrCodeText The url shortened associated to the qr.
     * @param size The size of the qr image width and height have the same dimension.
     * @return Mono object with the qr image in Base64 String format.
     */
    fun generateQR(qrCodeText: String): Mono<String> {
        val result = Mono.fromSupplier { generateQRString(qrCodeText, 400) }
            .subscribeOn(Schedulers.elastic())
            .timeout(Duration.ofSeconds(1L),
                Mono.error<String>(ServiceUnavailableError("QR image termporarily unavailable"))).block()!!
        return Mono.just(result)
    }



    /**
     * Generates and returns a QR image in Base64 string format
     * of the text.
     * @param qrCodeText Url shortened associated to the qr.
     * @param size Size of the qr image width and height have the same dimension.
     * @return QR image in Base64 String format.
     */
    @Cacheable("qrs", key = "#qrCodeText")
    fun generateQRString(qrCodeText: String, size: Int): String {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        val byteMatrix = QRCodeWriter().encode(qrCodeText, BarcodeFormat.QR_CODE, size, size)
        // Create object baos
        val baos = ByteArrayOutputStream()
        baos.use {
            // Convert the Base64 String image directly in qr format
            MatrixToImageWriter.writeToStream(byteMatrix, "png", baos)
            baos.flush()
            return Base64.getEncoder().encodeToString(baos.toByteArray())
        }
    }


    /**
     * Returns true if and only if the shortened url can be redirected.
     * @param su Shortened url.
     * @return true if and only if the url shortened can be redirected
     *          (active and safe) and otherwise returns false
     */
    fun canRedirect(su: ShortURL): Boolean = su.active && su.safe!!


    /**
     * Returns a url with the result of checking the url with
     * the safe browsing service.
     * @param su Shortened url.
     * @return Mono object with the result of checking the url
     *          with safe browsing service.
     */
    fun checkSafeBrowsing(su: ShortURL): Mono<ShortURL> =
        Mono.fromSupplier { safeBrowsing(su) }
        .filter(this::canRedirect)
        .flatMap(shortURLRepository::markGood)


    /**
     * Returns the url with its safeness.
     * @param su Shortened url.
     * @return Url with its safeness.
     */
    @Cacheable("safeURLs", key = "#su.id", unless = "!#result.safe")
    fun safeBrowsing(su: ShortURL): ShortURL {
        val safeness = su.target?.let { isSafe(it) }
        su.safe = safeness
        su.active = safeness!!
        return su
    }


     /**
      * Returns true if the url is safe or otherwise returns false.
     * @param url Url which is going to be checked if it's safe.
     * @return true if the url is safe or otherwise returns false.
     */
    fun isSafe(url: String): Boolean {
        // Check if the url is safe or not
        val mapClient = mapOf("clientId" to "es.unizar.urlshortener", "clientVersion" to "1.0.0")
        val mapThreatInfo = mapOf("threatTypes" to listOf("MALWARE", "SOCIAL_ENGINEERING"),
                "platformTypes" to listOf("WINDOWS"),
                "threatEntryTypes" to listOf("URL"),
                "threatEntries" to listOf(mapOf("url" to url)))
        // khttp 0.1.0 doesn't allow async petitions, and there are no upgrades available
        val r = post("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=$safeBrowsingKey",
                data = JSONObject(mapOf("client" to mapClient, "threatInfo" to mapThreatInfo)), timeout = 5.0)

        // Return JSON with the response
        return JSONObject(r.text).length() == 0
    }

    /**
     * Validates the vanity of given shortened url.
     * @param su Shortened url.
     * @return Validated shortened url.
     */
    fun validateVanity(su: ShortURL): Mono<ShortURL> {
        val matchVanity = REGEX_BRACKET.findAll(su.id!!).toList()
        val matchUrl = REGEX_BRACKET.findAll(su.target!!).toList()
        if (matchVanity.count() > 0 && matchUrl.count() > 0) {
            // Same length
            if (matchVanity.count() != 1 || matchUrl.count() != 1) {
                return Mono.error(BadRequestError("URL and vanity should only have one group"))
            }
            // Not repeated
            val stringVanity = matchVanity.get(0).value
            val stringUrl = matchUrl.get(0).value
            if (stringVanity != stringUrl) {
                return Mono.error(BadRequestError("Vanity groups don't match"))
            }
            // Matches are one to one (also modify target & hash)
            su.target = su.target!!.replace(stringUrl, "{0}")
            su.id = su.id!!.replace(stringVanity, "{0}")
        }
        return Mono.just(su)
    }

    /**
     * Checks every 600 seconds the safeness of the urls saved
     * in the cache.
     */
    @Scheduled(fixedRate = 600000)
    fun reviewSafeURLs() {

        // Create a connection with the Redis cache
        val port = env?.getProperty("spring.redis.port")
        var jedis = Jedis(env?.getProperty("spring.redis.host"), port!!.toInt())

        // For each key in the redis server
        for (cachedString in jedis.keys("safeURLs::*")) {

            // Obtain the associated url
            val parts = cachedString.split(":", limit = 3)
            val cachedId = parts[2]
            with(obtainUrl(cachedId)) {

                // If the associated url is not safe, remove the url from the cache
                if (!target?.let { isSafe(it) }!!) {
                    target?.let { removeUrl(it) }
                }
            }
        }
    }

    /**
     * Returns the cached url identified with "id"
     * @param id Identifier of the url
     * @return cached url identified with "id"
     */
    @Cacheable("safeURLs", key = "#id")
    fun obtainUrl(id: String): ShortURL {
        return ShortURL()
    }

    /**
     * Removes the cached url identified with "id"
     * @param id Identifier of the url
     */
    @CacheEvict("safeURLs", key = "#id")
    fun removeUrl(id: String) {
    }
}
