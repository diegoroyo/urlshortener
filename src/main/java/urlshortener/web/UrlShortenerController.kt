package urlshortener.web

import java.net.URI
import javax.servlet.http.HttpServletRequest
import javax.validation.constraints.Pattern
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.beans.factory.annotation.Value
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
import urlshortener.exception.NotFoundError
import urlshortener.service.ClickService
import urlshortener.service.ShortURLService
import org.springframework.web.util.UriTemplate

@RestController
class UrlShortenerController(private val shortUrlService: ShortURLService, private val clickService: ClickService) {

    private val REGEX_BROWSER = Regex("(?i)(firefox|msie|chrome|safari)[\\/\\s]([\\d.]+)")
    private val REGEX_OS = Regex("Windows|Linux|Mac")

    @Value("\${spring.server.host}")
    lateinit var serverIp: String
    @Value("\${spring.server.port}")
    lateinit var serverPort: String

    @PostMapping("/manage/link")
    fun shortener(
        @RequestParam(value = "url", required = true) url: String,
        @RequestParam(value = "vanity", required = false) vanity: String?,
        request: HttpServletRequest
    ): Mono<ShortURL> {
        return shortUrlService.save(url, request.remoteAddr, vanity)
    }

    @GetMapping(value = ["/{id:(?!swagger-ui|index)\\w+}", "/{id:(?!swagger-ui|index)\\w+}/{vanity:\\w+}"])
    fun redirectTo(
        @PathVariable id: String,
        @PathVariable vanity: String?,
        request: HttpServletRequest,
        @RequestHeader(value = "User-Agent", required = false) userAgent: String?,
        @RequestHeader(value = "Referer", required = false) referer: String?
    ): ResponseEntity<Unit> {
        var su: ShortURL? = null
        vanity?.let {
            su = shortUrlService.findByKey(id + "/{0}").block()!!
            su?.target = su?.target!!.replace("{0}", vanity)
        } ?: run {
            su = shortUrlService.findByKey(id).block()!!
        }
        // Get info from HTTP headers passed and IP
        val ip = request.remoteAddr
        var browser: String? = null
        var platform: String? = null
        userAgent?.let {
            browser = REGEX_BROWSER.find(userAgent)?.value
            platform = REGEX_OS.find(userAgent)?.value
        }
        // Save and redirect
        clickService.saveClick(su?.id!!, ip, referer, browser, platform)
        val h = with(HttpHeaders()) {
            location = URI.create(su?.target!!)
            this
        }
        return ResponseEntity.status(HttpStatus.valueOf(su?.mode!!)).headers(h).build()
    }

    @GetMapping("/manage/qr")
    fun generateQr(
        @RequestParam(value = "url", required = true)
        @Pattern(regexp = "^http://\$serverIp:\$serverPort/.*") url: String
    ): Mono<String>? {
        shortUrlService.findByKey(url.substring("http://\$serverIp:\$serverPort/".length))
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
