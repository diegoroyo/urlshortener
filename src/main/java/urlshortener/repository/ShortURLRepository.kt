package urlshortener.repository

import kotlin.collections.List
import reactor.core.publisher.Mono
import urlshortener.domain.ShortURL

public interface ShortURLRepository {

    public fun findByKey(id: String): Mono<ShortURL>

    public fun findByTarget(target: String): List<ShortURL>

    public fun save(su: ShortURL): Mono<ShortURL>

    public fun mark(su: ShortURL, safeness: Boolean): ShortURL?

    public fun update(su: ShortURL)

    public fun delete(id: String)

    public fun count(): Long?

    public fun list(limit: Long?, offset: Long?): List<ShortURL>?
}
