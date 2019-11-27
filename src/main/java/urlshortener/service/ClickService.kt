package urlshortener.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import urlshortener.domain.Click
import urlshortener.domain.ShortURL
import urlshortener.domain.ShortURLNotFound
import reactor.core.publisher.Mono
import urlshortener.repository.ClickRepository

@Service
public class ClickService(private val clickRepository: ClickRepository) {

    private val log: Logger = LoggerFactory.getLogger(ClickService::class.java)

    public fun saveClick(shortId: String, ip: String): Mono<Click> {
        var cl: Click = ClickBuilder().shortId(shortId).createdNow().ip(ip).build()
        cl = clickRepository.save(cl).block()!!
        log.info("[" + shortId + "] saved with id [ " + cl.clickId + " ]")
        return Mono.just(cl)
    }

    public fun getClicksFromURL(shortId: String) = clickRepository.findByShortURL(shortId)
}
