package urlshortener.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import urlshortener.domain.ShortURL;
import java.sql.ResultSet;
import urlshortener.repository.ShortURLRepository;
import kotlin.collections.*;

@Repository
public class ShortURLRepositoryImpl(val jdbc: JdbcTemplate) : ShortURLRepository {

    private val log = LoggerFactory.getLogger(ClickRepositoryImpl::class.java)

    val rowMapper: RowMapper<ShortURL> = RowMapper {
        rs: ResultSet, _ -> // rowNum: Int
            ShortURL(
                rs.getString("id"), rs.getString("target"),
                rs.getDate("created"), rs.getInt("mode"),
                rs.getBoolean("active"), rs.getBoolean("safe"),
                rs.getString("ip"), rs.getString("country")
            )
    }
    
    override fun findByKey(id: String) : ShortURL? {
        try {
            return jdbc.queryForObject("SELECT * FROM shorturl WHERE id=?", rowMapper, id)
        } 
        catch (e: Exception) {
            log.debug("When select for key {}", id, e)
            return null
        }
    }

    override fun findByTarget(target: String) : List<ShortURL> {
        try {
            return jdbc.query("SELECT * FROM shorturl WHERE target = ?", rowMapper, mutableListOf<kotlin.Any>(target)) as List<ShortURL>
        } 
        catch (e: Exception) {
            log.debug("When select for target " + target, e)
            return emptyList<ShortURL>()
        }
    }

    override fun save(su: ShortURL) : ShortURL? {
        try {
            jdbc.update("INSERT INTO shorturl VALUES (?,?,?,?,?,?,?,?)",
                    su.id, su.target, su.created, su.mode,
                    su.active, su.safe, su.IP, su.country)
        } 
        catch (e: DuplicateKeyException) {
            log.debug("When insert for key {}", su.id, e);
            return su
        } 
        catch (e: Exception) {
            log.debug("When insert", e)
            return null
        }
        return su
    }

    override fun mark(su: ShortURL, safeness: Boolean) : ShortURL? {
        try {
            jdbc.update("UPDATE shorturl SET safe=? WHERE id=?", safeness, su.id)
            var res = ShortURL()
            BeanUtils.copyProperties(su, res)
            DirectFieldAccessor(res).setPropertyValue("safe", safeness)
            return res
        } 
        catch (e: Exception) {
            log.debug("When update", e)
            return null
        }
    }

    override fun update(su: ShortURL) {
        try {
            jdbc.update(
                    "update shorturl set target=?, created=?, mode=?, active=?, safe=?, ip=?, country=? where id=?",
                    su.target, su.created, su.mode, su.active,
                    su.safe, su.IP, su.country, su.id)
        }
         catch (e: Exception) {
            log.debug("When update for hash {}", su.id, e);
        }
    }

    override fun delete(id: String) {
        try {
            jdbc.update("delete from shorturl where id=?", id)
        } 
        catch (e: Exception) {
            log.debug("When delete for id {}", id, e)
        }
    }

    override fun count() : Long? {
        try {
            return jdbc.queryForObject("select count(*) from shorturl", Long::class.java) as Long
        } 
        catch (e: Exception) {
            log.debug("When counting", e);
        }
        return -1L;
    }

    override fun list(limit: Long?, offset: Long?) : List<ShortURL>? {
        try {
            return jdbc.query("SELECT * FROM shorturl LIMIT ? OFFSET ?", rowMapper, mutableListOf<Long?>(limit, offset)) as List<ShortURL>
        } 
        catch (e: Exception) {
            log.debug("When select for limit {} and offset {}", limit, offset, e);
            return emptyList<ShortURL>()
        }
    }

}