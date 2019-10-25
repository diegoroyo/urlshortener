package urlshortener.repository;

import urlshortener.domain.Click;

import kotlin.collections.List;

public interface ClickRepository {

    public fun findByHash(hash: String) :List<Click> 

    public fun clicksByHash(hash: String) :Long

    public fun save(cl: Click) : Click?

    public fun update(cl: Click)

    public fun delete(id: Long)

    public fun deleteAll();

    public fun count() :Long

    public fun list(limit: Long, offset: Long) :List<Click> 
}