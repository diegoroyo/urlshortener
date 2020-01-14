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
     * Stores the click in the database.
     * @param shortId Identifier of the url.
     * @param clickIp IP of the click.
     * @param referer Referer of the click.
     * @param browser Browser of the click.
     * @param platform Platform of the click.
     * @return Mono object which represents the click created.
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
     * Returns the clicks from a url.
     * @param shortId Identifier of the url.
     * @param pageNumber Number of pages.
     * @param pageSize Size of each page.
     * @returns Clicks from a url.
     */
    fun getClicksFromURL(
        shortId: String,
        pageNumber: Int,
        pageSize: Int
    ): Flux<Click> {
        // If the number of pages or size of page is negative, throw an exception
        if (pageNumber < 0 || pageSize < 0) {
            throw BadRequestError("Invalid page")
        }
        return clickRepository.findByShortURL(shortId, PageRequest.of(pageNumber, pageSize))
    }
}
