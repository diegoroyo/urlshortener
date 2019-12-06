package urlshortener.repository

import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import urlshortener.domain.Click

interface ClickRepository {

    fun findByShortURL(id: String, page: Pageable): Flux<Click>

    fun save(cl: Click): Mono<Click>

    fun update(cl: Click): Mono<Void>

    fun delete(id: Long): Mono<Void>

    fun count(): Mono<Long>
}
