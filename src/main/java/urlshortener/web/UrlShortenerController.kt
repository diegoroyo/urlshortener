package urlshortener.web

import java.net.URI
import javax.servlet.http.HttpServletRequest
import javax.validation.constraints.Pattern
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
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
        val l: ShortURL = shortUrlService.findByKey(id).block()!!
        clickService.saveClick(id, request.getRemoteAddr())
        val h = HttpHeaders()
        h.setLocation(URI.create(l.target))
        return ResponseEntity.status(HttpStatus.valueOf(l.mode!!)).headers(h).build()
    }

    // TODO sustituir pattern localhost por una constante
    @GetMapping("/qr")
    fun generateQr(
        @RequestParam(value = "url", required = true)
        @Pattern(regexp = "^http://localhost:8080/.*") url: String
    ): Mono<String> {
        shortUrlService.findByKey(url.substring("http://localhost:8080/".length))
        return shortUrlService.generateQR(url)
    }

    @GetMapping("/statistics")
    fun getStatistics(
        @RequestParam(value = "short", required = true) short: String
    ): Flux<Click> = clickService.getClicksFromURL(short)
}
