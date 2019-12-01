package urlshortener.config

import org.davidmoten.rx.jdbc.Database
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import urlshortener.repository.ClickRepository
import urlshortener.repository.ShortURLRepository
import urlshortener.repository.impl.ClickRepositoryImpl
import urlshortener.repository.impl.ShortURLRepositoryImpl
import org.springframework.context.annotation.DependsOn
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@Configuration
public class PersistenceConfiguration {

    @Value("\${spring.datasource.url}")
    lateinit var DB_CONNECTION: String
    
    @Bean
    fun dbInitialization(): Database {
        // TODO maxPoolNumber
        return Database.from(DB_CONNECTION, 100)
    }

    @Bean
    fun shortURLRepository(): ShortURLRepository {
        return ShortURLRepositoryImpl(dbInitialization())
    }

    @Bean
    fun clickRepository(): ClickRepository {
        return ClickRepositoryImpl(dbInitialization())
    }
}
