/*
 *******************************************
 *** Urlshortener - Web Engineering ********
 *** Authors: Name  ************************
 *** Andrew Mackay - 737069 ****************
 *** Ruben Rodríguez Esteban - 737215 ******
 *** Diego Royo Meneses - 740388 ***********
 *** Course: 2019 - 2020 *******************
 *******************************************
 */ 

package urlshortener.web

import com.google.common.hash.Hashing
import java.net.URI
import javax.servlet.http.HttpServletRequest
import javax.validation.constraints.Pattern
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import urlshortener.domain.Click
import urlshortener.domain.ShortURL
import urlshortener.exception.BadRequestError
import urlshortener.exception.ConflictError
import urlshortener.exception.NotFoundError
import urlshortener.service.ClickService
import urlshortener.service.ShortURLService
import java.nio.charset.StandardCharsets


// Urlshortener controller


@RestController
class UrlShortenerController(private val shortUrlService: ShortURLService, private val clickService: ClickService) {

    // Expresions to control the regex of operating systems and client browsers
    private val REGEX_BROWSER = Regex("(?i)(firefox|msie|chrome|safari)[\\/\\s]([\\d.]+)")
    private val REGEX_OS = Regex("Windows|Linux|Mac")

    @Value("\${spring.server.host}")
    lateinit var serverIp: String
    @Value("\${spring.server.port}")
    lateinit var serverPort: String

    @PostMapping("/manage/link")
    @ResponseStatus(HttpStatus.CREATED)
    fun shortener(
        @RequestParam(value = "url", required = true) url: String,
        @RequestParam(value = "vanity", required = false) vanity: String?,
        request: HttpServletRequest
    ): Mono<ShortURL> {
        if (vanity.isNullOrEmpty()) {
            return try {
                // Check if the URL exists
                val hash = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString()

                // If it exists, return the saved instance
                Mono.just(shortUrlService.findByKey(hash).block()!!)
            } catch (e: Exception) {

                // If doesnt exists, save the new URL
                shortUrlService.save(url, request.remoteAddr, vanity)
            }
        } else {
            return try {
                // Check if vanity exists
                val savedURLMono = shortUrlService.findByKey(vanity)
                val savedURL = savedURLMono.block()!!
                if (savedURL.target.equals(url)) {
                    // If it exists, but it belongs to the same URL, return the saved instance
                    Mono.just(savedURL)
                } else {
                    // If it exists, but it belongs to a different URL throw an exception
                    throw ConflictError("There already exists a different URL with that vanity")
                }
            } catch (e: Exception) {
                // If it doesnt exist, save the new URL
                shortUrlService.save(url, request.remoteAddr, vanity)
            }
        }
    }



     /**
     * Redirection to new url shortened
     */
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
        if (!su?.active!! || !su?.safe!!) {
            return ResponseEntity.notFound().build()
        }
        // Get info from HTTP headers passed and IP
        val ip = request.remoteAddr
        var browser: String? = null
        var platform: String? = null
        userAgent?.let {
            // Get browser and platform from http headers
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


     /**
     * Generation of qr image
     */
    @GetMapping("/manage/qr")
    fun generateQr(
        @RequestParam(value = "url", required = true) url: String
    ): Mono<String> {
        val regexQR = Regex("^http://$serverIp:$serverPort/.*")
        if (regexQR.containsMatchIn(url)) {
            shortUrlService.findByKey(url.substring("http://$serverIp:$serverPort/".length))
            return shortUrlService.generateQR(url)
        } else {
            throw BadRequestError("Invalid URL format")
        }
    }


    /**
     * Get statistics
     */
    @GetMapping("/manage/statistics")
    fun getStatistics(
        @RequestParam(value = "short", required = true) short: String,
        @RequestParam(value = "pageNumber", required = true) pageNumber: Int,
        @RequestParam(value = "pageSize", required = true) pageSize: Int
    ): Flux<Click> = clickService.getClicksFromURL(short, pageNumber, pageSize)
}
