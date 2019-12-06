package urlshortener.repository

import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import urlshortener.domain.Click

public interface ClickRepository {

    public fun findByShortURL(id: String, page: Pageable): Flux<Click>

    public fun save(cl: Click): Mono<Click>

    public fun update(cl: Click): Mono<Void>

    public fun delete(id: Long): Mono<Void>

    public fun count(): Mono<Long>
}
