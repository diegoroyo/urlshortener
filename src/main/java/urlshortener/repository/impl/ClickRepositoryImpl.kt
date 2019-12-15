package urlshortener.repository.impl

import java.sql.ResultSet
import org.davidmoten.rx.jdbc.Database
import org.davidmoten.rx.jdbc.ResultSetMapper
import org.springframework.data.domain.Pageable
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import reactor.adapter.rxjava.RxJava2Adapter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import urlshortener.exception.InternalServerError
import urlshortener.domain.Click
import urlshortener.repository.ClickRepository

@Repository
class ClickRepositoryImpl(val db: Database) : ClickRepository {

    private val log = LoggerFactory.getLogger(ClickRepositoryImpl::class.java)

    private val rowMapper: ResultSetMapper<Click> = ResultSetMapper {
        rs: ResultSet ->
            Click(
                rs.getInt("clickId"), rs.getString("shortId"),
                rs.getDate("created"), rs.getString("referer"),
                rs.getString("browser"), rs.getString("platform"),
                rs.getString("ip")
            )
    }

    override fun findByShortURL(id: String, page: Pageable): Flux<Click> {
        val sort = page.sort.toString().replace(":","").split(' ')
        return Flux.from(
            db.select("SELECT * FROM click WHERE shortId=? ORDER BY ? LIMIT ? OFFSET ?")
                .parameters(id, sort,
                            page.pageSize, page.pageNumber * page.pageSize)
                .get(rowMapper)
        )
    }

    override fun save(cl: Click): Mono<Click> {
        cl.clickId = Mono.from(db.update(
            "INSERT INTO CLICK (shortid, created, referer, browser, platform, ip)" +
            "VALUES (?, ?, ?, ?, ?, ?)")
            .parameters(cl.shortId, cl.created, cl.referer, cl.browser, cl.platform, cl.ip)
            .returnGeneratedKeys()
            .getAs(Int::class.java)
        ).block()
        return Mono.just(cl)
    }

    override fun update(cl: Click): Mono<Void> = RxJava2Adapter.completableToMono(
        db.update(
            "update click set shortId=?, created=?, referer=?, browser=?, platform=?, ip=?" +
            "where clickId=?")
            .parameters(cl.shortId, cl.created, cl.referer, cl.browser, cl.platform, cl.ip, cl.clickId)
            .complete()
    )

    override fun delete(id: Long): Mono<Void> = RxJava2Adapter.completableToMono(db.update(
            "delete from click where clickId=?"
        ).parameters(id).complete())

    override fun count(): Mono<Long> = Mono.from(db.select(
            "select count(*) from click"
        ).getAs(Long::class.java))
}
