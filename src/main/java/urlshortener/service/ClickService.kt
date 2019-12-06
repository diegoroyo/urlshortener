package urlshortener.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.PageRequest
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import urlshortener.domain.Click
import urlshortener.repository.ClickRepository

@Service
public class ClickService(private val clickRepository: ClickRepository) {

    private val log: Logger = LoggerFactory.getLogger(ClickService::class.java)

    public fun saveClick(
        shortId: String,
        ip: String,
        referer: String?,
        browser: String?,
        platform: String?
    ): Mono<Click> {
        var cl: Click = ClickBuilder().shortId(shortId).createdNow().referer(referer)
                        .browser(browser).platform(platform).build()
        cl = clickRepository.save(cl).block()!!
        log.info("[" + shortId + "] saved with id [ " + cl.clickId + " , browser " + cl.browser +
                     " , platform " + cl.platform + " , referer " + cl.referer + "]")

        return Mono.just(cl)
    }

    public fun getClicksFromURL(
        shortId: String,
        pageNumber: Int,
        pageSize: Int,
        sortAttr: String?,
        ascending: Boolean?
    ): Flux<Click> {
        var page: Pageable?
        if (sortAttr == null || ascending == null) {
            page = PageRequest.of(pageNumber, pageSize, Sort.by("clickId").ascending())
        } else {
            if (ascending) {
                page = PageRequest.of(pageNumber, pageSize, Sort.by(sortAttr).ascending())
            } else {
                page = PageRequest.of(pageNumber, pageSize, Sort.by(sortAttr).descending())
            }
        }
        print(page.sort.toString().replace(":", ""))
        return clickRepository.findByShortURL(shortId, page)
    }
}
