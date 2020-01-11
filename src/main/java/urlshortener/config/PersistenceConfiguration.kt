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


/**
 * Control the persistence of the information
 */

@Configuration
public class PersistenceConfiguration {

    // Sure the persistence from short url and click repositories

    @Value("\${spring.datasource.url}")
    lateinit var DB_CONNECTION: String
    
    @Bean
    fun dbInitialization(): Database {
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
