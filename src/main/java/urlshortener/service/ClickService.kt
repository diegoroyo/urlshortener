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

    private val log: Logger = LoggerFactory.getLogger(ClickService::class.java)

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
