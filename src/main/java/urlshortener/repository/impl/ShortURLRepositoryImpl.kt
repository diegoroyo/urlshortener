package urlshortener.repository.impl

import java.sql.ResultSet
import kotlin.collections.*
import org.davidmoten.rx.jdbc.Database
import org.davidmoten.rx.jdbc.ResultSetMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import reactor.adapter.rxjava.RxJava2Adapter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import urlshortener.domain.ShortURL
import urlshortener.repository.ShortURLRepository

@Repository
public class ShortURLRepositoryImpl(val db: Database) : ShortURLRepository {

    private val log = LoggerFactory.getLogger(ClickRepositoryImpl::class.java)

    private val rowMapper: ResultSetMapper<ShortURL> = ResultSetMapper {
        rs: ResultSet ->
            ShortURL(
                rs.getString("id"), rs.getString("target"),
                rs.getDate("created"), rs.getInt("mode"),
                rs.getBoolean("active"), rs.getBoolean("safe"),
                rs.getString("ip"), rs.getString("country")
            )
    }

    override fun findByKey(id: String): Mono<ShortURL> = Mono.from(db.select("SELECT * FROM shorturl WHERE id=?").parameters(id).get(rowMapper))

    override fun findByTarget(target: String): Flux<ShortURL> =
        Flux.from(db.select("SELECT * FROM shorturl WHERE target = ?").parameters(target).get(rowMapper))

    override fun save(su: ShortURL): Mono<ShortURL> {
        RxJava2Adapter.completableToMono(
            db.update("INSERT INTO shorturl VALUES (?,?,?,?,?,?,?,?)")
              .parameters(su.id, su.target, su.created, su.mode, su.active, su.safe, su.IP, su.country)
              .complete()
        ).block()
        return Mono.just(su)
    }

    override fun mark(su: ShortURL, safeness: Boolean): Mono<ShortURL> {
        RxJava2Adapter.completableToMono(
            db.update("UPDATE shorturl SET safe=? WHERE id=?")
              .parameters(safeness, su.id)
              .complete()
        ).block()
        su.safe = safeness
        return Mono.just(su)
    }

    override fun update(su: ShortURL): Mono<Void> = RxJava2Adapter.completableToMono(
            db.update("update shorturl set target=?, created=?, mode=?, active=?, safe=?, ip=?, country=? where id=?")
            .parameters(su.target, su.created, su.mode, su.active, su.safe, su.IP, su.country, su.id)
            .complete()
    )

    override fun delete(id: String): Mono<Void> =
        RxJava2Adapter.completableToMono(
            db.update("delete from shorturl where id=?")
            .parameters(id)
            .complete()
        )

    override fun count(): Mono<Long> =
        Mono.from(db.select("select count(*) from shorturl").getAs(Long::class.java))

    override fun list(limit: Long, offset: Long): Flux<ShortURL> =
        Flux.from(db.select("SELECT * FROM shorturl LIMIT ? OFFSET ?").parameters(limit, offset).get(rowMapper))
}
