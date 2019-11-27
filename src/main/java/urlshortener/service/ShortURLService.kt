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
import urlshortener.domain.ShortURL
import urlshortener.domain.ShortURLInvalid
import urlshortener.repository.ShortURLRepository

@Service
public class ShortURLService(private val shortURLRepository: ShortURLRepository) {

    @Value("\${google.safebrowsing.api_key}")
    lateinit var safeBrowsingKey: String

    public fun findByKey(id: String): Mono<ShortURL> = shortURLRepository.findByKey(id)

    public fun save(url: String, ip: String, vanity: String? = null): Mono<ShortURL> {
        val urlValidator = UrlValidator(arrayOf("http", "https"))
        if (!urlValidator.isValid(url) || !checkSafeBrowsing(url)) {
            throw ShortURLInvalid
        }
        // TODO comprobar vanity valido
        val su = ShortURLBuilder()
                .target(url, vanity)
                .createdNow()
                .temporaryRedirect()
                .ip(ip)
                .unknownCountry() // TODO
                .build()
        return shortURLRepository.save(su)
    }

    // TODO catchear esto
    public fun generateQR(qrCodeText: String, size: Int = 400): Mono<String> {

        // Create the ByteMatrix for the QR-Code that encodes the given String
        val byteMatrix = QRCodeWriter().encode(qrCodeText, BarcodeFormat.QR_CODE, size, size)

        // Make the BufferedImage that are to hold the QRCode
        val image = BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        image.createGraphics()

        // Creation of the image
        val graphics = image.getGraphics()
        graphics.setColor(Color.WHITE)
        graphics.fillRect(0, 0, size, size)
        graphics.setColor(Color.BLACK)

        // Paints the image qr with the colors with the byteMatrix
        for (i in 0..size - 1) {
            for (j in 0..size - 1) {
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
    public fun checkSafeBrowsing(url: String): Boolean {
        val mapClient = mapOf("clientId" to "es.unizar.urlshortener", "clientVersion" to "1.0.0")
        val mapThreatInfo = mapOf("threatTypes" to listOf("MALWARE", "SOCIAL_ENGINEERING"),
                                  "platformTypes" to listOf("WINDOWS"),
                                  "threatEntryTypes" to listOf("URL"),
                                  "threatEntries" to listOf(mapOf("url" to url)))
        var r = post("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=$safeBrowsingKey",
            data = JSONObject(mapOf("client" to mapClient, "threatInfo" to mapThreatInfo)))
        return JSONObject(r.text).length() == 0
    }
}
