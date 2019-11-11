package urlshortener.web

import org.apache.commons.validator.routines.UrlValidator
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import urlshortener.domain.ShortURL
import urlshortener.service.ClickService
import urlshortener.service.ShortURLService
import java.net.URI
import javax.servlet.http.HttpServletRequest

@RestController
class UrlShortenerController(private val shortUrlService: ShortURLService, private val clickService: ClickService) {

    @PostMapping("/link")
    fun shortener(@RequestParam("url") url: String,
                 @RequestParam(value = "vanity", required = false) vanity: String?,
                 request: HttpServletRequest): ResponseEntity<ShortURL> {
        val urlValidator = UrlValidator(arrayOf("http", "https"));
        if (urlValidator.isValid(url) && shortUrlService.checkSafeBrowsing(url)) {
            val su = if (vanity.isNullOrBlank()) {
                shortUrlService.save(url, request.getRemoteAddr());
            } else {
                // TODO comprobar que vanity es valido
                shortUrlService.save(url, request.getRemoteAddr(), vanity);
            }
            val h = HttpHeaders();
            h.setLocation(URI(url));
            return ResponseEntity<ShortURL>(su, h, HttpStatus.CREATED);
        } else {
            return ResponseEntity<ShortURL>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id:(?!link|index).*}")
    fun redirectTo(@PathVariable id: String, request: HttpServletRequest) : ResponseEntity<Unit> {
        val l: ShortURL? = shortUrlService.findByKey(id);
        if (l != null) {
            clickService.saveClick(id, request.getRemoteAddr());
            val h = HttpHeaders();
            h.setLocation(URI.create(l.target));
            return ResponseEntity<Unit>(h, HttpStatus.valueOf(l.mode!!));
        } else {
            return ResponseEntity<Unit>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/qr")
    fun generateQr(@RequestParam(value = "url", required = true) url: String,
                   request: HttpServletRequest): ResponseEntity<String> {
        // TODO sustituir localhost
        val start: String = "http://localhost:8080/"
        if (url.startsWith(start) &&
            shortUrlService.findByKey(url.substring(start.length)) != null) {
            val qrImage: String = shortUrlService.generateQR(url);
            return ResponseEntity<String>(qrImage, HttpStatus.OK);
        } else {
            return ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/statistics")
    fun generateQr(@RequestParam(value = "short", required = true) short: String,
                   request: HttpServletRequest): ResponseEntity<List<Click>> {
        val clickList = clickService.getClicksFromURL(short)
        if (clickList != null) {
            return ResponseEntity<List<Click>>(clickList, HttpStatus.OK);
        } else {
            return ResponseEntity<List<Click>>(HttpStatus.NOT_FOUND);
        }
    }

}