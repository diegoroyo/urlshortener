package urlshortener.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO
import khttp.post
import org.apache.commons.validator.routines.UrlValidator
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import urlshortener.domain.ShortURL
import urlshortener.exception.NotFoundError
import urlshortener.exception.BadRequestError
import urlshortener.repository.ShortURLRepository
import org.springframework.web.util.UriTemplate

@Service
public class ShortURLService(private val shortURLRepository: ShortURLRepository) {

    private val REGEX_BRACKET = Regex("\\{[a-z]+\\}", setOf(RegexOption.IGNORE_CASE))

    @Value("\${google.safebrowsing.api_key}")
    var safeBrowsingKey: String? = null

    public fun findByKey(id: String): Mono<ShortURL> = shortURLRepository.findByKey(id)

    public fun save(url: String, ip: String, vanity: String? = null): Mono<ShortURL> {
        // Valid URL
        val urlValidator = UrlValidator(arrayOf("http", "https"))
        if (!urlValidator.isValid(url)) {
            throw BadRequestError("Invalid URL to short")
        }
        // Valid vanity
        if (!vanity.isNullOrBlank() && vanity == "api") {
            throw BadRequestError("Vanity cannot start with \"api/\"")
        }
        var su = ShortURLBuilder()
                .target(url, vanity)
                .createdNow()
                .temporaryRedirect()
                .ip(ip)
                .build()
        // Treat vanity for URL templates
        if (!vanity.isNullOrBlank()) {
            validateVanity(su)
        }
        // Save and start checking safe browsing
        // TODO probablemente se puede hacer de otra manera uniendo monos
        return shortURLRepository.save(su).then(checkSafeBrowsing(su))
    }

    // TODO catchear esto
    public fun generateQR(qrCodeText: String, size: Int = 400): Mono<String> {

        // Create the ByteMatrix for the QR-Code that encodes the given String
        val byteMatrix = QRCodeWriter().encode(qrCodeText, BarcodeFormat.QR_CODE, size, size)

        // Make the BufferedImage that are to hold the QRCode
        val image = BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        image.createGraphics()

        // Creation of the image
        val graphics = image.graphics
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, size, size)
        graphics.color = Color.BLACK

        // Paints the image qr with the colors with the byteMatrix
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1)
                }
            }
        }
        val baos = ByteArrayOutputStream()
        baos.use {
            ImageIO.write(image, "png", baos)
            baos.flush()
            return Mono.just(Base64.getEncoder().encodeToString(baos.toByteArray()))
        }
    }

    // TODO cachear esto + proceso de fondo que lea la cache
    public fun checkSafeBrowsing(su: ShortURL): Mono<ShortURL> {
        val mapClient = mapOf("clientId" to "es.unizar.urlshortener", "clientVersion" to "1.0.0")
        val mapThreatInfo = mapOf("threatTypes" to listOf("MALWARE", "SOCIAL_ENGINEERING"),
                                  "platformTypes" to listOf("WINDOWS"),
                                  "threatEntryTypes" to listOf("URL"),
                                  "threatEntries" to listOf(mapOf("url" to su.target!!)))
        // khttp 0.1.0 doesn't allow async petitions, and there are no upgrades available
        val r = post("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=$safeBrowsingKey",
            data = JSONObject(mapOf("client" to mapClient, "threatInfo" to mapThreatInfo)))
        if (JSONObject(r.text).length() == 0) {
            shortURLRepository.mark(su, true)
        }
        return Mono.just(su)
    }

    public fun validateVanity(su: ShortURL): ShortURL {
        val matchVanity = REGEX_BRACKET.find(su.id!!)?.groups
        val matchUrl = REGEX_BRACKET.find(su.target!!)?.groups
        if (matchVanity != null && matchUrl != null) {
            // Same length
            if (matchUrl.count() != matchVanity.count()) {
                throw BadRequestError("URL and vanity don't have same number of groups")
            }
            // Not repeated
            var strings = mutableListOf<String>()
            for (i in 0..matchVanity.count() - 1) {
                val string = matchVanity.get(i)!!.value
                if (strings.contains(string)) {
                    throw BadRequestError("Group $string appears more than once")
                }
                strings.add(matchVanity.get(i)!!.value)
            }
            // Matches are one to one (also modify target & hash)
            for (i in 0..matchVanity.count() - 1) {
                var found = false
                for (j in 0..matchUrl.count() - 1) {
                    if (matchUrl.get(j)!!.value == strings[i]) {
                        su.target = su.target!!.replace(strings.get(i), "{$i}")
                        su.id = su.id!!.replace(strings.get(i), "{$i}")
                        found = true
                        break
                    }
                }
                if (!found) {
                    throw BadRequestError("Group ${strings.get(i)} doesn't have a match")
                }
            }
        }
        return su
    }

    public fun findTemplate(string: String): Flux<ShortURL> =
    shortURLRepository.listTemplates().filter {
            candidate: ShortURL ->
                UriTemplate(candidate.id!!).matches(string)
    }

}
