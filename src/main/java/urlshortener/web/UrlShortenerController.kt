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
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import urlshortener.domain.Click
import urlshortener.domain.ShortURL
import urlshortener.service.ClickService
import urlshortener.service.ShortURLService

@RestController
class UrlShortenerController(private val shortUrlService: ShortURLService, private val clickService: ClickService) {

    private val REGEX_BROWSER = Regex(pattern = "(?i)(firefox|msie|chrome|safari)[\\/\\s]([\\d.]+)")
    private val REGEX_OS = Regex(pattern = "Windows|Linux|Mac")

    @PostMapping("/api/link")
    fun shortener(
        @RequestParam(value = "url", required = true) url: String,
        @RequestParam(value = "vanity", required = false) vanity: String?,
        request: HttpServletRequest
    ): Mono<ShortURL> = shortUrlService.save(url, request.getRemoteAddr(), vanity)

    @GetMapping("/{id:(?!api|index).*}")
    fun redirectTo(
        @PathVariable id: String,
        request: HttpServletRequest,
        @RequestHeader(value = "User-Agent", required = false) userAgent: String?,
        @RequestHeader(value = "Referer", required = false) referer: String?
    ): ResponseEntity<Unit> {
        // Ensure that click comes from a valid URL
        val l: ShortURL = shortUrlService.findByKey(id).block()!!
        // Get info from HTTP headers passed and IP
        val ip = request.getRemoteAddr()
        var browser: String? = null
        var platform: String? = null
        if (userAgent != null) {
            browser = REGEX_BROWSER.find(userAgent)?.value
            platform = REGEX_OS.find(userAgent)?.value
        }
        // Save and redirect
        clickService.saveClick(id, ip, referer, browser, platform)
        val h = HttpHeaders()
        h.setLocation(URI.create(l.target))
        return ResponseEntity.status(HttpStatus.valueOf(l.mode!!)).headers(h).build()
    }

    // TODO sustituir pattern localhost por una constante
    @GetMapping("/api/qr")
    fun generateQr(
        @RequestParam(value = "url", required = true)
        @Pattern(regexp = "^http://localhost:8080/.*") url: String
    ): Mono<String> {
        shortUrlService.findByKey(url.substring("http://localhost:8080/".length))
        return shortUrlService.generateQR(url)
    }

    @GetMapping("/api/statistics")
    fun getStatistics(
        @RequestParam(value = "short", required = true) short: String,
        @RequestParam(value = "pageNumber", required = true) pageNumber: Int,
        @RequestParam(value = "pageSize", required = true) pageSize: Int,
        @RequestParam(value = "sort", required = false) sort: String?,
        @RequestParam(value = "ascending", required = false) ascending: Boolean?
    ): Flux<Click> = clickService.getClicksFromURL(short, pageNumber, pageSize, sort, ascending)
}
