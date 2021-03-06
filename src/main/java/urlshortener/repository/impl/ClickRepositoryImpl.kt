/*
 *******************************************
 *** Urlshortener - Web Engineering ********
 *** Authors: Name  ************************
 *** Andrew Mackay - 737069 ****************
 *** Ruben Rodríguez Esteban - 737215 ******
 *** Diego Royo Meneses - 740388 ***********
 *** Course: 2019 - 2020 *******************
 *******************************************
 */ 

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
import urlshortener.domain.Click
import urlshortener.repository.ClickRepository


@Repository
class ClickRepositoryImpl(val db: Database) : ClickRepository {

    // Logger to control the trace of events
    private val log = LoggerFactory.getLogger(ClickRepositoryImpl::class.java)

    // ResultsetMapper of urls shortened
    private val rowMapper: ResultSetMapper<Click> = ResultSetMapper {
        rs: ResultSet ->
            Click(
                rs.getInt("clickId"), rs.getString("shortId"),
                rs.getDate("created"), rs.getString("referer"),
                rs.getString("browser"), rs.getString("platform"),
                rs.getString("ip")
            )
    }

    /**
     * Returns click by url with pages.
     * @param id is the id of the url shortened.
     * @param page Pageabe object.
     * @return List of clicks of the url.
     */
    override fun findByShortURL(id: String, page: Pageable): Flux<Click> = Flux.from(
            db.select("SELECT * FROM click WHERE shortId=? LIMIT ? OFFSET ?")
                .parameters(id,
                            page.pageSize, page.pageNumber * page.pageSize)
                .get(rowMapper)
        )
    
    /**
     * Saves a click.
     * @param cl Click to save.
     * @return Mono object with the click saved in the database.
     */
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

    /**
     * @param cl is a click.
     * @return Mono object with the click updated in the database.
     */
    override fun update(cl: Click): Mono<Void> = RxJava2Adapter.completableToMono(
        db.update(
            "update click set shortId=?, created=?, referer=?, browser=?, platform=?, ip=?" +
            "where clickId=?")
            .parameters(cl.shortId, cl.created, cl.referer, cl.browser, cl.platform, cl.ip, cl.clickId)
            .complete()
    )

    /**
     * Delete a click from the database.
     * @param id is the id of the click which is going to be deleted.
     */
    override fun delete(id: Long): Mono<Void> = RxJava2Adapter.completableToMono(db.update(
            "delete from click where clickId=?"
        ).parameters(id).complete())

    /**
     * @return Mono object with the number of clicks of the database.
     */
    override fun count(): Mono<Long> = Mono.from(db.select(
            "select count(*) from click"
        ).getAs(Long::class.java))
}
