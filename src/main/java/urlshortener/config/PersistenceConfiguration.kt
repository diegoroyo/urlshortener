package urlshortener.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import urlshortener.repository.ClickRepository
import urlshortener.repository.ShortURLRepository
import urlshortener.repository.impl.ClickRepositoryImpl
import urlshortener.repository.impl.ShortURLRepositoryImpl

@Configuration
public class PersistenceConfiguration(val jdbc: JdbcTemplate) {

    @Bean
    fun shortURLRepository(): ShortURLRepository {
        return ShortURLRepositoryImpl(jdbc)
    }

    @Bean
    fun clickRepository(): ClickRepository {
        return ClickRepositoryImpl(jdbc)
    }
}
