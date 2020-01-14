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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import urlshortener.domain.Click

interface ClickRepository {


    /**
     * Returns the clicks of the url.
     * @param id Id of the url.
     * @param page Pageable object.
     */
    fun findByShortURL(id: String, page: Pageable): Flux<Click>


    /**
     * Saves a click.
     * @param cl Click.
     * @returns Mono object with the click saved.
     */
    fun save(cl: Click): Mono<Click>



    /**
     * Update a click.
     * @param cl Click.
     */
    fun update(cl: Click): Mono<Void>



    /**
     * Deletes a click.
     * @param id Identifier of the click.
     */
    fun delete(id: Long): Mono<Void>



    /**
     * Returns the number of clicks.
     * @returns the number of clicks.
     */
    fun count(): Mono<Long>
}
