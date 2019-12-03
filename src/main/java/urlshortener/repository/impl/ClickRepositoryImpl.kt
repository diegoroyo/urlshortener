package urlshortener.repository.impl

import java.sql.ResultSet
import org.davidmoten.rx.jdbc.Database
import org.davidmoten.rx.jdbc.ResultSetMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import reactor.adapter.rxjava.RxJava2Adapter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import urlshortener.domain.Click
import urlshortener.repository.ClickRepository

@Repository
public class ClickRepositoryImpl(val db: Database) : ClickRepository {

    private val log = LoggerFactory.getLogger(ClickRepositoryImpl::class.java)

    private val rowMapper: ResultSetMapper<Click> = ResultSetMapper {
        rs: ResultSet ->
            Click(
                rs.getInt("clickId"), rs.getString("shortId"),
                rs.getDate("created"), rs.getString("referrer"),
                rs.getString("browser"), rs.getString("platform"),
                rs.getString("ip"), rs.getString("country")
            )
    }

    // TODO error mapping

    override fun findByShortURL(id: String): Flux<Click> = Flux.from(
            db.select("SELECT * FROM click WHERE shortId=?")
                .parameters(id)
                .get(rowMapper)
        )

    override fun save(cl: Click): Mono<Click> {
        cl.clickId = Mono.from(db.update(
            "INSERT INTO CLICK (shortid, created, referrer, browser, platform, ip, country)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?)")
            .parameters(cl.shortId, cl.created, cl.referrer, cl.browser, cl.platform, cl.ip, cl.country)
            .returnGeneratedKeys()
            .getAs(Int::class.java)
        ).block()
        return Mono.just(cl)
    }

    override fun update(cl: Click): Mono<Void> = RxJava2Adapter.completableToMono(
        db.update(
            "update click set shortId=?, created=?, referrer=?, browser=?, platform=?, ip=?, country=?" +
            "where clickId=?")
            .parameters(cl.shortId, cl.created, cl.referrer, cl.browser, cl.platform, cl.ip, cl.country, cl.clickId)
            .complete()
    )

    override fun delete(id: Long): Mono<Void> = RxJava2Adapter.completableToMono(db.update(
            "delete from click where clickId=?"
        ).parameters(id).complete())

    override fun count(): Mono<Long> = Mono.from(db.select(
            "select count(*) from click"
        ).getAs(Long::class.java))

    override fun list(limit: Long, offset: Long): Flux<Click> = Flux.from(db.select(
            "SELECT * FROM click LIMIT ? OFFSET ?"
        ).parameters(limit, offset).get(rowMapper))
}
