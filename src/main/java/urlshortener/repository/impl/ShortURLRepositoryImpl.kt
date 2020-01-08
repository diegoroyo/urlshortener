package urlshortener.repository.impl

import java.sql.ResultSet
import org.davidmoten.rx.jdbc.Database
import org.davidmoten.rx.jdbc.ResultSetMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import reactor.adapter.rxjava.RxJava2Adapter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import urlshortener.exception.NotFoundError
import urlshortener.domain.ShortURL
import urlshortener.exception.ConflictError
import urlshortener.repository.ShortURLRepository

@Repository
class ShortURLRepositoryImpl(val db: Database) : ShortURLRepository {

    private val log = LoggerFactory.getLogger(ClickRepositoryImpl::class.java)

    private val rowMapper: ResultSetMapper<ShortURL> = ResultSetMapper {
        rs: ResultSet ->
            ShortURL(
                rs.getString("id"), rs.getString("target"),
                rs.getDate("created"), rs.getInt("mode"),
                rs.getBoolean("active"), rs.getBoolean("safe"),
                rs.getString("ip")
            )
    }

    override fun findByKey(id: String): Mono<ShortURL> = Mono.from(
        db.select("SELECT * FROM shorturl WHERE id=?")
          .parameters(id)
          .get(rowMapper)
    ).switchIfEmpty(Mono.error(NotFoundError("There are no ShortURLs with that key")))

    override fun save(su: ShortURL): Mono<ShortURL> {
        try {
            RxJava2Adapter.completableToMono(
                    db.update("INSERT INTO shorturl VALUES (?,?,?,?,?,?,?)")
                            .parameters(su.id, su.target, su.created, su.mode, su.active, su.safe, su.IP)
                            .complete()
            ).block()
        } catch (e: Exception) {
            throw ConflictError("There already exists a URL with that vanity")
        }
        return Mono.just(su)
    }

    override fun markGood(su: ShortURL): Mono<ShortURL> {
        RxJava2Adapter.completableToMono(
            db.update("UPDATE shorturl SET safe=?, active=? WHERE id=?")
              .parameters(true, true, su.id)
              .complete()
        ).block()
        return Mono.just(su)
    }

    override fun markBad(su: ShortURL): Mono<ShortURL> {
        RxJava2Adapter.completableToMono(
            db.update("UPDATE shorturl SET safe=?, active=? WHERE id=?")
              .parameters(false, false, su.id)
              .complete()
        ).block()
        return Mono.just(su)
    }

    override fun update(su: ShortURL): Mono<Void> = RxJava2Adapter.completableToMono(
            db.update("update shorturl set target=?, created=?, mode=?, active=?, safe=?, ip=? where id=?")
            .parameters(su.target, su.created, su.mode, su.active, su.safe, su.IP, su.id)
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
        
    override fun listTemplates(): Flux<ShortURL> = Flux.from(
        db.select("SELECT * FROM shorturl WHERE id LIKE '%{0}%'")
        .get(rowMapper)
    )
}
