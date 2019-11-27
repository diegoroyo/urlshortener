package urlshortener.repository

import kotlin.collections.List
import reactor.core.publisher.Mono
import urlshortener.domain.Click

public interface ClickRepository {

    public fun findByShortURL(id: String): Mono<List<Click>>

    public fun save(cl: Click): Mono<Click>

    public fun update(cl: Click)

    public fun delete(id: Long?)

    public fun deleteAll()

    public fun count(): Long?

    public fun list(limit: Long?, offset: Long?): List<Click>?
}
