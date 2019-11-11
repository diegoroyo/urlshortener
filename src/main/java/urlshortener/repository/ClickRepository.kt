package urlshortener.repository;

import urlshortener.domain.Click;

import kotlin.collections.List;

public interface ClickRepository {

    public fun findByShortURL(id: String) : List<Click>?

    public fun save(cl: Click) : Click?

    public fun update(cl: Click)

    public fun delete(id: Long?)

    public fun deleteAll()

    public fun count() : Long?

    public fun list(limit: Long?, offset: Long?) : List<Click>?
}