package urlshortener.repository;

import urlshortener.domain.ShortURL;

import kotlin.collections.List;

public interface ShortURLRepository {

    public fun findByKey(id: String) : ShortURL?

    public fun findByTarget(target: String) : List<ShortURL>

    public fun save(su: ShortURL) : ShortURL?

    public fun mark(su: ShortURL, safeness: Boolean) : ShortURL?

    public fun update(su: ShortURL)

    public fun delete(id: String)

    public fun count() : Long?

    public fun list(limit: Long?, offset: Long?) : List<ShortURL>?

}