package urlshortener.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import urlshortener.web.UrlShortenerController;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.net.HttpURLConnection;

import java.util.Base64;
 
import javax.imageio.ImageIO;
 
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import khttp.post;
import org.json.JSONObject

@Service
public class ShortURLService(private val shortURLRepository: ShortURLRepository) {

    @Value("\${google.safebrowsing.api_key}")
    lateinit var safeBrowsingKey: String;

    public fun findByKey(id: String) : ShortURL? = shortURLRepository.findByKey(id);

    public fun save(url: String, ip: String, vanity: String? = null) : ShortURL? {
        val su: ShortURL? = ShortURLBuilder()
                .target(url, vanity)
                .createdNow()
                .temporaryRedirect()
                .ip(ip)
                .unknownCountry() // TODO
                .build();
        return shortURLRepository.save(su!!);
    }

    // TODO catchear esto
    @Throws(WriterException::class, IOException::class)
    public fun generateQR(qrCodeText: String , size: Int = 400) : String {
        
        // Create the ByteMatrix for the QR-Code that encodes the given String
        var hintMap: HashMap<EncodeHintType, ErrorCorrectionLevel> = HashMap<EncodeHintType, ErrorCorrectionLevel> () 
        var qrCodeWriter: QRCodeWriter = QRCodeWriter()

        val byteMatrix: BitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap)

        // Make the BufferedImage that are to hold the QRCode
        val matrixWidth: Int = byteMatrix.getWidth()

        var image: BufferedImage = BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB)
        image.createGraphics()

        // Creation of the image
        var graphics: Graphics2D = image.getGraphics() as Graphics2D
        graphics.setColor(Color.WHITE)
        graphics.fillRect(0, 0, matrixWidth, matrixWidth)

        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK)

        // Paints the image qr with the colors with the byteMatrix
        for (i in 0 until matrixWidth - 1 step 1) {
            for (j in 0 until matrixWidth - 1 step 1) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1)
                }
            }
        }
        val baos = ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush()
        val array : ByteArray = baos.toByteArray()
        baos.close()

        return Base64.getEncoder().encodeToString(array)
    }

    

    public fun checkSafeBrowsing(url: String) : Boolean {
        val mapClient = mapOf("clientId" to "es.unizar.urlshortener", "clientVersion" to "1.0.0")
        val mapThreatInfo = mapOf("threatTypes" to listOf<String>("MALWARE", "SOCIAL_ENGINEERING"),
                                  "platformTypes" to listOf<String>("WINDOWS"),
                                  "threatEntryTypes" to listOf<String>("URL"),
                                  "threatEntries" to listOf<Map<String, String>>(mapOf("url" to url)));
        print(mapOf("client" to mapClient, "threatInfo" to mapThreatInfo).toString());
        var r = post("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=$safeBrowsingKey",
            data = JSONObject(mapOf("client" to mapClient, "threatInfo" to mapThreatInfo)));
        print(r.text)
        return true
    }

}
