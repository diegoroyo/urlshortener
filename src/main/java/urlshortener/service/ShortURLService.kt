package urlshortener.service;

import org.springframework.stereotype.Service;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import urlshortener.web.UrlShortenerController;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
 
import javax.imageio.ImageIO;
 
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

@Service
public class ShortURLService(private val shortURLRepository: ShortURLRepository) {

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


    @Throws(WriterException::class, IOException::class)
    public fun generateQR(qrCodeText: String , size: Int) : Graphics2D {
        
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
        return graphics 
    }
}
