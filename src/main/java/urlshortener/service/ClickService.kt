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

package urlshortener.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import urlshortener.domain.Click
import urlshortener.repository.ClickRepository
import urlshortener.exception.BadRequestError

@Service
class ClickService(private val clickRepository: ClickRepository) {

    // Logger to control the trace of events
    private val log: Logger = LoggerFactory.getLogger(ClickService::class.java)


    /**
     * Stores the click in the database
     * @param shortId
     * @param clickIp
     * @param referer
     * @param browser
     * @param platform
     * @return a mono object which represents the click created
     */
    fun saveClick(
        shortId: String,
        clickIp: String,
        referer: String?,
        browser: String?,
        platform: String?
    ): Mono<Click> {
        var cl: Click = ClickBuilder().shortId(shortId).createdNow().referer(referer)
                        .browser(browser).platform(platform).ip(clickIp).build()
        cl = clickRepository.save(cl).block()!!
        log.info("[" + shortId + "] saved with id [ " + cl.clickId + " , browser " + cl.browser +
                     " , platform " + cl.platform + " , referer " + cl.referer + "]")

        return Mono.just(cl)
    }



     /**
     * get clicks from a click with a specific parameters
     * @param shortId
     * @param pageNumber
     * @param pageSize
     * @returns the number of clicks
     */
    fun getClicksFromURL(
        shortId: String,
        pageNumber: Int,
        pageSize: Int
    ): Flux<Click> {
        if (pageNumber < 0 || pageSize < 0) {
            throw BadRequestError("Invalid page")
        }
        return clickRepository.findByShortURL(shortId, PageRequest.of(pageNumber, pageSize))
    }
}
