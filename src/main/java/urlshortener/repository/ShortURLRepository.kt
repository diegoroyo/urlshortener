package urlshortener.repository

import org.springframework.data.domain.Pageable
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import urlshortener.domain.ShortURL

interface ShortURLRepository {

    fun findByKey(id: String): Mono<ShortURL>

    fun findByTarget(target: String): Flux<ShortURL>

    fun save(su: ShortURL): Mono<ShortURL>

    fun markGood(su: ShortURL): Mono<ShortURL>

    fun markBad(su: ShortURL): Mono<ShortURL>

    fun update(su: ShortURL): Mono<Void>

    fun delete(id: String): Mono<Void>

    fun count(): Mono<Long>

    fun listTemplates(): Flux<ShortURL>
}
