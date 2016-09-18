package urlshortener.common.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.util.List;

import urlshortener.common.domain.ShortURL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;
import static urlshortener.common.repository.fixture.ShortURLFixture.badUrl;
import static urlshortener.common.repository.fixture.ShortURLFixture.url1;
import static urlshortener.common.repository.fixture.ShortURLFixture.url1modified;
import static urlshortener.common.repository.fixture.ShortURLFixture.url2;
import static urlshortener.common.repository.fixture.ShortURLFixture.url3;
import static urlshortener.common.repository.fixture.ShortURLFixture.urlSafe;
import static urlshortener.common.repository.fixture.ShortURLFixture.urlSponsor;

public class ShortURLRepositoryTests {

	private EmbeddedDatabase db;
	private ShortURLRepository repository;
	private JdbcTemplate jdbc;

	@Before
	public void setup() {
		db = new EmbeddedDatabaseBuilder().setType(HSQL)
				.addScript("schema-hsqldb.sql").build();
		jdbc = new JdbcTemplate(db);
		repository = new ShortURLRepositoryImpl(jdbc);
	}

	@Test
	public void thatSavePersistsTheShortURL() {
		assertNotNull(repository.save(url1()));
		assertSame(jdbc.queryForObject("select count(*) from SHORTURL",
				Integer.class), 1);
	}

	@Test
	public void thatSaveSponsor() {
		assertNotNull(repository.save(urlSponsor()));
		assertSame(jdbc.queryForObject("select sponsor from SHORTURL",
				String.class), urlSponsor().getSponsor());
	}

	@Test
	public void thatSaveSafe() {
		assertNotNull(repository.save(urlSafe()));
		assertSame(
				jdbc.queryForObject("select safe from SHORTURL", Boolean.class),
				true);
		repository.mark(urlSafe(), false);
		assertSame(
				jdbc.queryForObject("select safe from SHORTURL", Boolean.class),
				false);
		repository.mark(urlSafe(), true);
		assertSame(
				jdbc.queryForObject("select safe from SHORTURL", Boolean.class),
				true);
	}

	@Test
	public void thatSaveADuplicateHashIsSafelyIgnored() {
		repository.save(url1());
		assertNotNull(repository.save(url1()));
		assertSame(jdbc.queryForObject("select count(*) from SHORTURL",
				Integer.class), 1);
	}

	@Test
	public void thatErrorsInSaveReturnsNull() {
		assertNull(repository.save(badUrl()));
		assertSame(jdbc.queryForObject("select count(*) from SHORTURL",
				Integer.class), 0);
	}

	@Test
	public void thatFindByKeyReturnsAURL() {
		repository.save(url1());
		repository.save(url2());
		ShortURL su = repository.findByKey(url1().getHash());
		assertNotNull(su);
		assertSame(su.getHash(), url1().getHash());
	}

	@Test
	public void thatFindByKeyReturnsNullWhenFails() {
		repository.save(url1());
		assertNull(repository.findByKey(url2().getHash()));
	}

	@Test
	public void thatFindByTargetReturnsURLs() {
		repository.save(url1());
		repository.save(url2());
		repository.save(url3());
		List<ShortURL> sul = repository.findByTarget(url1().getTarget());
		assertEquals(sul.size(), 2);
		sul = repository.findByTarget(url3().getTarget());
		assertEquals(sul.size(), 1);
		sul = repository.findByTarget("dummy");
		assertEquals(sul.size(), 0);
	}
	
	@Test
	public void thatDeleteDelete() {
		repository.save(url1());
		repository.save(url2());
		repository.delete(url1().getHash());
		assertEquals(repository.count().intValue(), 1);
		repository.delete(url2().getHash());
		assertEquals(repository.count().intValue(), 0);
	}

	@Test
	public void thatUpdateUpdate() {
		repository.save(url1());
		ShortURL su = repository.findByKey(url1().getHash());
		assertEquals(su.getTarget(), "http://www.unizar.es/");
		repository.update(url1modified());
		su = repository.findByKey(url1().getHash());
		assertEquals(su.getTarget(), "http://www.unizar.org/");
	}
	
	@After
	public void shutdown() {
		db.shutdown();
	}

}
