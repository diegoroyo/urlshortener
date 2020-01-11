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
     * @param id is the id of the click
     * @param page is the pageable page
     */
    fun findByShortURL(id: String, page: Pageable): Flux<Click>


    /**
     * @param cl is a click
     * @returns a mono object with the click saved
     */
    fun save(cl: Click): Mono<Click>



    /**
     * update a click
     * @param cl is a click
     */
    fun update(cl: Click): Mono<Void>



    /**
     * delete a click
     * @returns a mono with the click removed
     */
    fun delete(id: Long): Mono<Void>



    /**
     * @returns the number of clicks
     */
    fun count(): Mono<Long>
}
