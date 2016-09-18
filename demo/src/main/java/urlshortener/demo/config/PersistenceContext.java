package urlshortener.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ClickRepositoryImpl;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.repository.ShortURLRepositoryImpl;

@Configuration
public class PersistenceContext {

	@Autowired
    protected JdbcTemplate jdbc;

	@Bean
	ShortURLRepository shortURLRepository() {
		return new ShortURLRepositoryImpl(jdbc);
	}
 	
	@Bean
	ClickRepository clickRepository() {
		return new ClickRepositoryImpl(jdbc);
	}
	
}
