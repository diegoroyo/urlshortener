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
import org.springframework.web.util.UriTemplate

@RestController
class UrlShortenerController(private val shortUrlService: ShortURLService, private val clickService: ClickService) {

    private val REGEX_BROWSER = Regex("(?i)(firefox|msie|chrome|safari)[\\/\\s]([\\d.]+)")
    private val REGEX_OS = Regex("Windows|Linux|Mac")

    @PostMapping("/manage/link")
    fun shortener(
        @RequestParam(value = "url", required = true) url: String,
        @RequestParam(value = "vanity", required = false) vanity: String?,
        request: HttpServletRequest
    ): Mono<ShortURL> {
        return shortUrlService.save(url, request.remoteAddr, vanity)
    }

    @GetMapping("/{id:(?!index).*}")
    fun redirectTo(
        @PathVariable id: String,
        request: HttpServletRequest,
        @RequestHeader(value = "User-Agent", required = false) userAgent: String?,
        @RequestHeader(value = "Referer", required = false) referer: String?
    ): ResponseEntity<Unit> {
        var l: ShortURL? = shortUrlService.findTemplate(id).take(1).blockFirst()
        // Treat id/target differently depending on whether the short url is a template or not
        var reconstructedId: String?
        var target: String?
        if (l == null) {
            // No template
            l = shortUrlService.findByKey(id).block()!!
            target = l.target
            reconstructedId = l.id
        } else {
            // Change template and create original id to save on click table
            val map = UriTemplate(l.id!!).match(id)
            target = l.target
            reconstructedId = l.id
            for ((key, value) in map) {
                target = target!!.replace("{" + key + "}", value)
                reconstructedId = reconstructedId!!.replace(value, "{" + key + "}")
            }
        }
        // Get info from HTTP headers passed and IP
        val ip = request.remoteAddr
        var browser: String? = null
        var platform: String? = null
        if (userAgent != null) {
            browser = REGEX_BROWSER.find(userAgent)?.value
            platform = REGEX_OS.find(userAgent)?.value
        }
        // Save and redirect
        clickService.saveClick(reconstructedId!!, ip, referer, browser, platform)
        val h = HttpHeaders()
        h.location = URI.create(target!!)
        return ResponseEntity.status(HttpStatus.valueOf(l.mode!!)).headers(h).build()
    }

    // TODO sustituir pattern localhost por una constante
    @GetMapping("/manage/qr")
    fun generateQr(
        @RequestParam(value = "url", required = true)
        @Pattern(regexp = "^http://localhost:8080/.*") url: String
    ): Mono<String>? {
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
