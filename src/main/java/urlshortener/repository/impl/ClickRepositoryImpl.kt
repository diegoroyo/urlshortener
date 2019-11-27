package urlshortener.repository.impl

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import kotlin.collections.List
import org.slf4j.LoggerFactory
import org.springframework.beans.DirectFieldAccessor
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import urlshortener.domain.Click
import urlshortener.domain.ClickStorageError
import urlshortener.repository.ClickRepository

@Repository
public class ClickRepositoryImpl(val jdbc: JdbcTemplate) : ClickRepository {

    private val log = LoggerFactory.getLogger(ClickRepositoryImpl::class.java)

    private val rowMapper: RowMapper<Click> = RowMapper {
        rs: ResultSet, _ -> // rowNum: Int
            Click(
                rs.getLong("clickId"), rs.getString("shortId"),
                rs.getDate("created"), rs.getString("referrer"),
                rs.getString("browser"), rs.getString("platform"),
                rs.getString("ip"), rs.getString("country")
            )
    }

    override fun findByShortURL(id: String): Mono<List<Click>> {
        try {
            return Mono.just(jdbc.query("SELECT * FROM click WHERE shortId=?", rowMapper, id))
        } catch (e: Exception) {
            log.debug("When select for shortId " + id, e)
            return Mono.just(emptyList())
        }
    }

    override fun save(cl: Click): Mono<Click> {
        try {
            val holder: KeyHolder = GeneratedKeyHolder()
            jdbc.update(
                {
                    conn ->
                    var ps: PreparedStatement = conn.prepareStatement(
                            "INSERT INTO CLICK (shortid, created, referrer, browser, platform, ip, country)" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS)
                    ps.setString(1, cl.shortId)
                    ps.setDate(2, cl.created)
                    ps.setString(3, cl.referrer)
                    ps.setString(4, cl.browser)
                    ps.setString(5, cl.platform)
                    ps.setString(6, cl.ip)
                    ps.setString(7, cl.country)
                    ps
                },
                holder)
            if (holder.getKeys() != null) {
                DirectFieldAccessor(cl).setPropertyValue("clickId", (holder.getKeys()!!.get("clickId") as Number).toLong())
            } else {
                log.debug("Key from database is null")
            }
        } catch (e: Exception) {
            log.debug("When insert a click", e)
            throw ClickStorageError
        }
        return Mono.just(cl)
    }

    override fun update(cl: Click) {
        log.info("ID2: {} navegador: {} SO: {} Date: {}", cl.clickId, cl.browser, cl.platform, cl.created)
        try {
            jdbc.update(
                    "update click set shortId=?, created=?, referrer=?, browser=?, platform=?, ip=?, country=? where clickId=?",
                    cl.shortId, cl.created, cl.referrer,
                    cl.browser, cl.platform, cl.ip,
                    cl.country, cl.clickId)
        } catch (e: Exception) {
            log.info("When update for clickId " + cl.clickId, e)
        }
    }

    override fun delete(id: Long?) {
        try {
            jdbc.update("delete from click where clickId=?", id)
        } catch (e: Exception) {
            log.debug("When delete for clickId " + id, e)
        }
    }

    override fun deleteAll() {
        try {
            jdbc.update("delete from click")
        } catch (e: Exception) {
            log.debug("When delete all", e)
        }
    }

    override fun count(): Long? {
        try {
            return jdbc.queryForObject("select count(*) from click", Long::class.java) as Long
        } catch (e: Exception) {
            log.debug("When counting", e)
        }
        return -1L
    }

    override fun list(limit: Long?, offset: Long?): List<Click>? {
        try {
            return jdbc.query("SELECT * FROM click LIMIT ? OFFSET ?", rowMapper, mutableListOf<Long?>(limit, offset)) as List<Click>
        } catch (e: Exception) {
            log.debug("When select for limit " + limit + " and offset " + offset, e)
            return emptyList<Click>()
        }
    }
}
