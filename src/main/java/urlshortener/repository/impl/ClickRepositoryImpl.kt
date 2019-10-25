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
import kotlin.collections.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;

@Repository
public class ClickRepositoryImpl : ClickRepository {

    private val log = LoggerFactory.getLogger(ClickRepositoryImpl::class.java)
    
    private val rowMapper :RowMapper<Click> = (rs, rowNum) -> Click(rs.getLong("id"), rs.getString("hash"),
            rs.getDate("created"), rs.getString("referrer"),
            rs.getString("browser"), rs.getString("platform"),
            rs.getString("ip"), rs.getString("country"));

    val jdbc: JdbcTemplate

    constructor(jdbc: JdbcTemplate) : ClickRepositoryImpl {
        this.jdbc = jdbc;
    }
    
    override fun findByHash(hash:String) : List<Click>  {
        var list: List<Click> = emptyList()
        try {
            return jdbc.query("SELECT * FROM click WHERE hash=?", mutableListOf(<kotlin.Any>(hash)) as List<Click>;
        } 
        catch (e: Exception) {
            log.debug("When select for hash " + hash, e);
            return list 
        }
    }

    
    
    override fun save(cl: Click) :Click? {
        try {
            var holder: KeyHolder = GeneratedKeyHolder();
            jdbc.update(conn -> var ps :PreparedStatement::class.java = conn.prepareStatement("INSERT INTO CLICK VALUES (?, ?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS)
                ps.setNull(1, Types.BIGINT)
                ps.setString(2, cl.hash)
                ps.setDate(3, cl.created)
                ps.setString(4, cl.referrer)
                ps.setString(5, cl.browser)
                ps.setString(6, cl.platform)
                ps.setString(7, cl.ip);
                ps.setString(8, cl.country)
                return ps
            , holder)
            if (holder.getKey() != null) {
                DirectFieldAccessor(cl).setPropertyValue("id", holder.getKey().longValue())
            } 
            else {
                log.debug("Key from database is null")
            }
        } 
        catch (e: DuplicateKeyException) {
            log.debug("When insert for click with id " + cl.id, e)
            return cl;
        } 
        catch (e: Exception) {
            log.debug("When insert a click", e)
            return null
        }
        return cl;
    }
    

    override fun update(cl: Click) {
        log.info("ID2: {} navegador: {} SO: {} Date: {}", cl.id, cl.browser, cl.platform, cl.created);
        try {
            jdbc.update(
                    "update click set hash=?, created=?, referrer=?, browser=?, platform=?, ip=?, country=? where id=?",
                    cl.hash, cl.created, cl.referrer,
                    cl.browser, cl.platform, cl.ip,
                    cl.country, cl.id)

        } 
        catch (e Exception) {
            log.info("When update for id " + cl.id, e)
        }
    }
    

    override fun delete(id: Long) {
        try {
            jdbc.update("delete from click where id=?", id)
        } 
        catch (e: Exception) {
            log.debug("When delete for id " + id, e)
        }
    }

    @Override
    override fun deleteAll() {
        try {
            jdbc.update("delete from click")
        } 
        catch (e: Exception) {
            log.debug("When delete all", e)
        }
    }

    
    override fun count() :Long {
        try {
            return jdbc.queryForObject("select count(*) from click", Long::class.java) as Long
        } 
        catch (e: Exception) {
            log.debug("When counting", e)
        }
        return -1L;
    }


    override fun list(limit: Long, offset:Long) :List<Click> {
        var list: List<Click> = emptyList()
        try {
            return jdbc.query("SELECT * FROM click LIMIT ? OFFSET ?", rowMapper, mutableListOf<kotlin.Any>(limit, offset)) as List<Click>
        } 
        catch (e: Exception) {
            log.debug("When select for limit " + limit + " and offset " + offset, e);        
            return  list
        }
    }
    

    fun editFile(hash: String) :Long{
        try {
            return jdbc.queryForObject("select count(*) from click where hash = ?", mutableListOf<kotlin.Any>(hash), Long::class.java) as Long
        } 
        catch (e: Exception) {
            log.debug("When counting hash " + hash, e);
        }
        return -1L;
    }

}