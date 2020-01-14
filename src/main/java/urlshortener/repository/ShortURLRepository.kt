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

import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import urlshortener.domain.ShortURL


interface ShortURLRepository {

     /**
      * Returns the saved shortened url with identifier "id".
      * @param id Id of the url.
      * @returns a Mono object with the url found.
      */
    fun findByKey(id: String): Mono<ShortURL>

     /**
      * Saves a shortened url and returns the same url.
     * @param su Shortened url to save.
     * @returns Mono object with the url saved.
     */
    fun save(su: ShortURL): Mono<ShortURL>

    /**
     * Marks the shortened url as good.
     * @param id is the id of the url.
     * @returns Mono object with the url marked as good.
     */
    fun markGood(su: ShortURL): Mono<ShortURL>

    /**
     * Marks the shortened url as malicious.
     * @param su Id of the url.
     * @returns Mono object with the url marked as malicious.
     */
    fun markBad(su: ShortURL): Mono<ShortURL>


    /**
     * Updates a shortened url.
     * @param su Updated shortened url.
     * @return Updated shortened url.
     */
    fun update(su: ShortURL): Mono<Void>

     /**
     * Deletes the shortened url with identifier "id"
     * @param id Id of the shortened url.
     */
    fun delete(id: String): Mono<Void>

    /**
     * Returns the number of urls.
     * @return number of urls.
     */
    fun count(): Mono<Long>

    /**
     * Returns the list of urls.
     * @return list of urls.
     */
    fun listTemplates(): Flux<ShortURL>
}
