package urlshortener.web

import java.net.URI
import javax.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import urlshortener.domain.Click
import urlshortener.domain.ShortURL
import urlshortener.service.ClickService
import urlshortener.service.ShortURLService

@RestController
class UrlShortenerController(private val shortUrlService: ShortURLService, private val clickService: ClickService) {

    @PostMapping("/link")
    fun shortener(
        @RequestParam(value = "url", required = true) url: String,
        @RequestParam(value = "vanity", required = false) vanity: String?,
        request: HttpServletRequest
    ): Mono<ShortURL> = shortUrlService.save(url, request.getRemoteAddr(), vanity)

    @GetMapping("/{id:(?!link|index).*}")
    fun redirectTo(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<Unit> {
        val l: ShortURL? = shortUrlService.findByKey(id)
        return if (l != null) {
            clickService.saveClick(id, request.getRemoteAddr())
            val h = HttpHeaders()
            h.setLocation(URI.create(l.target))
            ResponseEntity.status(HttpStatus.valueOf(l.mode!!)).headers(h).build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/qr")
    fun generateQr(
        @RequestParam(value = "url", required = true) url: String
    ): ResponseEntity<String> {
        // TODO sustituir localhost
        val start: String = "http://localhost:8080/"
        return if (url.startsWith(start) &&
            shortUrlService.findByKey(url.substring(start.length)) != null) {
            val qrImage: String = shortUrlService.generateQR(url)
            ResponseEntity.ok(qrImage)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/statistics")
    fun getStatistics(
        @RequestParam(value = "short", required = true) short: String
    ): ResponseEntity<List<Click>> {
        val clickList = clickService.getClicksFromURL(short)
        return if (clickList != null) {
            ResponseEntity.ok(clickList)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
