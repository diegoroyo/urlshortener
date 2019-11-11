package urlshortener.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import urlshortener.domain.Click;
import urlshortener.repository.ClickRepository;
import java.sql.ResultSet;
import kotlin.collections.List;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;

@Repository
public class ClickRepositoryImpl(val jdbc: JdbcTemplate) : ClickRepository {

    private val log = LoggerFactory.getLogger(ClickRepositoryImpl::class.java)
    
    private val rowMapper : RowMapper<Click> = RowMapper {
        rs: ResultSet, _ -> // rowNum: Int
            Click(
                rs.getLong("clickId"), rs.getString("shortId"),
                rs.getDate("created"), rs.getString("referrer"),
                rs.getString("browser"), rs.getString("platform"),
                rs.getString("ip"), rs.getString("country")
            )
    }

    override fun findByShortURL(id: String) : List<Click>? {
        try {
            return jdbc.query("SELECT * FROM click WHERE shortId=?", rowMapper, mutableListOf<String>(id)) as List<Click>
        } 
        catch (e: Exception) {
            log.debug("When select for shortId " + id, e);
            return emptyList<Click>()
        }
    }

    override fun save(cl: Click) : Click? {
        println("a savear")
        try {
            var holder: KeyHolder = GeneratedKeyHolder();
            jdbc.update(
                { 
                    conn -> 
                    var ps: PreparedStatement = conn.prepareStatement(
                            "INSERT INTO CLICK VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS)
                    ps.setNull(1, Types.BIGINT)
                    ps.setString(2, cl.shortId)
                    ps.setDate(3, cl.created)
                    ps.setString(4, cl.referrer)
                    ps.setString(5, cl.browser)
                    ps.setString(6, cl.platform)
                    ps.setString(7, cl.ip)
                    ps.setString(8, cl.country)
                    ps
                },
                holder)
            if (holder.getKey() != null) {
                DirectFieldAccessor(cl).setPropertyValue("clickId", holder.getKey()!!.toLong())
            }
            else {
                log.debug("Key from database is null")
            }
            println("a dfsjfjsavear")
        } 
        catch (e: DuplicateKeyException) {
            log.debug("When insert for click with clickId " + cl.clickId, e)
            return cl
        } 
        catch (e: Exception) {
            log.debug("When insert a click", e)
            print("excepcion!!!!")
            println(e)
            return null
        }
        print("su click!!!!")
        println(cl)
        return cl
    }

    override fun update(cl: Click) {
        log.info("ID2: {} navegador: {} SO: {} Date: {}", cl.clickId, cl.browser, cl.platform, cl.created);
        try {
            jdbc.update(
                    "update click set shortId=?, created=?, referrer=?, browser=?, platform=?, ip=?, country=? where clickId=?",
                    cl.shortId, cl.created, cl.referrer,
                    cl.browser, cl.platform, cl.ip,
                    cl.country, cl.clickId)

        } 
        catch (e: Exception) {
            log.info("When update for clickId " + cl.clickId, e)
        }
    }
    
    override fun delete(id: Long?) {
        try {
            jdbc.update("delete from click where clickId=?", id)
        } 
        catch (e: Exception) {
            log.debug("When delete for clickId " + id, e)
        }
    }

    override fun deleteAll() {
        try {
            jdbc.update("delete from click")
        } 
        catch (e: Exception) {
            log.debug("When delete all", e)
        }
    }

    override fun count() : Long? {
        try {
            return jdbc.queryForObject("select count(*) from click", Long::class.java) as Long
        } 
        catch (e: Exception) {
            log.debug("When counting", e)
        }
        return -1L;
    }


    override fun list(limit: Long?, offset:Long?) : List<Click>? {
        try {
            return jdbc.query("SELECT * FROM click LIMIT ? OFFSET ?", rowMapper, mutableListOf<Long?>(limit, offset)) as List<Click>
        } 
        catch (e: Exception) {
            log.debug("When select for limit " + limit + " and offset " + offset, e);        
            return emptyList<Click>()
        }
    }

}