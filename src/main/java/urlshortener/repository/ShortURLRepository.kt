package urlshortener.repository

import kotlin.collections.List
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import urlshortener.domain.ShortURL

public interface ShortURLRepository {

    public fun findByKey(id: String): Mono<ShortURL>

    public fun findByTarget(target: String): Flux<ShortURL>

    public fun save(su: ShortURL): Mono<ShortURL>

    public fun mark(su: ShortURL, safeness: Boolean): Mono<ShortURL>

    public fun update(su: ShortURL): Mono<Void>

    public fun delete(id: String): Mono<Void>

    public fun count(): Mono<Long>

    public fun list(limit: Long, offset: Long): Flux<ShortURL>
}
