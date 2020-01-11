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


package urlshortener.repository

import org.springframework.data.domain.Pageable
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import urlshortener.domain.ShortURL

interface ShortURLRepository {


     /**
     * @param id is the id of the url
     * @returns a mono object with the url found
     */
    fun findByKey(id: String): Mono<ShortURL>



     /**
     * @param id is the id of the url
     * @returns a mono object with the url saved
     */
    fun save(su: ShortURL): Mono<ShortURL>


    /**
     * @param id is the id of the url
     * @returns a mono object with the url marked as good
     */
    fun markGood(su: ShortURL): Mono<ShortURL>


    /**
     * @param id is the id of the url
     * @returns a mono object with the url marked as malicious
     */
    fun markBad(su: ShortURL): Mono<ShortURL>


     /**
     * update the url shortened
     * @param su is the url shortened
     */
    fun update(su: ShortURL): Mono<Void>


     /**
     * delete the id  of the url shortened
     * @param id is the id of the url shortened
     */
    fun delete(id: String): Mono<Void>


    /**
     * count the urls
     */
    fun count(): Mono<Long>


    /**
     * list the templates
     */
    fun listTemplates(): Flux<ShortURL>
}
