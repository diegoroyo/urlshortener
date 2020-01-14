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
import urlshortener.service.ClickService
import urlshortener.service.ShortURLService
import java.nio.charset.StandardCharsets


@RestController
class UrlShortenerController(private val shortUrlService: ShortURLService, private val clickService: ClickService) {

    // Expressions to control the regex of operating systems and client browsers
    private val REGEX_BROWSER = Regex("(?i)(firefox|msie|chrome|safari)[\\/\\s]([\\d.]+)")
    private val REGEX_OS = Regex("Windows|Linux|Mac")

    // Server IP
    @Value("\${spring.server.host}")
    lateinit var serverIp: String

    // Server port
    @Value("\${spring.server.port}")
    lateinit var serverPort: String

    /**
     * Saves and returns the shortened URL created with the passed url,
     * vanity and request.
     * @Param url url of the link.
     * @Param vanity Vanity name of the URL.
     * @Param request HTTP request information.
     * @Return If everything is correct it returns the shortened URL and
     * status 200, if the link or vanity are incorrect status 400 or if
     * the vanity already exists but belongs to a different URL 409.
     */
    @PostMapping("/manage/link")
    @ResponseStatus(HttpStatus.CREATED)
    fun shortener(
        @RequestParam(value = "url", required = true) url: String,
        @RequestParam(value = "vanity", required = false) vanity: String?,
        request: HttpServletRequest
    ): Mono<ShortURL> {
        if (vanity.isNullOrEmpty()) {
            // If the URL does not include a vanity name
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
            // If the URL does include a vanity name
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
     * Redirects the page to the URL that belongs to the vanity stored
     * in the database.
     * @Param id Name of the URL.
     * @Param vanity Vanirt name of the URL.
     * @Param userAgent User agent header.
     * @Param referer Referer header.
     * @Return 302 if OK, 404 if it doesn't exist in the database
     * or 400 if the url's format is incorrect.
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

        // Obtain URL from the database and replace vanities
        vanity?.let {
            su = shortUrlService.findByKey("$id/{0}").block()!!
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

        // Save click information and redirect
        clickService.saveClick(su?.id!!, ip, referer, browser, platform)
        val h = with(HttpHeaders()) {
            location = URI.create(su?.target!!)
            this
        }
        return ResponseEntity.status(HttpStatus.valueOf(su?.mode!!)).headers(h).build()
    }


     /**
     * Returns de QR image for the URL.
      * @Param url URL.
      * @Return QR image for the URL and 200 if OK, 404 if the
      * URL is not saved in the database or 400 if the URL has
      * an incorrect format.
     */
    @GetMapping("/manage/qr")
    fun generateQr(
        @RequestParam(value = "url", required = true) url: String
    ): Mono<String> {
         // Check that the URL has the correct pattern
        val regexQR = Regex("^http://$serverIp:$serverPort/.*")
        if (regexQR.containsMatchIn(url)) {
            // If the URL is correct, find it in the database
            shortUrlService.findByKey(url.substring("http://$serverIp:$serverPort/".length))
            // Generate and return it's QR
            return shortUrlService.generateQR(url)
        } else {
            // If the URL is incorrect, return a bad request error
            throw BadRequestError("Invalid URL format")
        }
    }


    /**
     * Returns the statistics for the URL with vanity "short".
     * @Param short Vanity of the URL
     * @Param pageNumber Number of pages
     * @Param pageSize Size of pages
     * @Return Statistics of the URL and status 200.
     */
    @GetMapping("/manage/statistics")
    fun getStatistics(
        @RequestParam(value = "short", required = true) short: String,
        @RequestParam(value = "pageNumber", required = true) pageNumber: Int,
        @RequestParam(value = "pageSize", required = true) pageSize: Int
    ): Flux<Click> = clickService.getClicksFromURL(short, pageNumber, pageSize)
}
