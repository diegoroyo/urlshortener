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

package urlshortener.web

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import reactor.core.publisher.Mono
import urlshortener.domain.ShortURL
import urlshortener.repository.ShortURLRepository
import urlshortener.service.ShortURLService
import org.junit.Test
import org.junit.Assert.assertEquals
import urlshortener.util.*
import urlshortener.exception.*
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.mockito.Mockito


@RunWith(SpringJUnit4ClassRunner::class)
open class VanityServiceTests {

    @Mock
    private val shortUrlRepository: ShortURLRepository? = null

    @InjectMocks
    private val shortUrlService: ShortURLService? = null

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @Test
    open fun saveValidVanity() {
        // Create a example URL
        val su = exampleURL()

        // When we save a URL, return the same URL
        `when`(shortUrlRepository!!.save(any(ShortURL::class.java))).then { Mono.just<ShortURL>(it.getArgument(0)) }

        // Save another URL with the same values
        val other1 = shortUrlService!!.save(su.target!!, su.IP!!, vanity=su.id!!).block()!!

        // Check that the returned URL and the first one are equal
        assertEquals(su.id, other1.id)
        assertEquals(su.target, other1.target)
    }

    @Test(expected=ConflictError::class)
    open fun saveInvalidVanity() {
        // Create an example URL
        val su = exampleURL()

        // When the URL is saved, it should return an exception
        `when`(shortUrlRepository!!.save(any(ShortURL::class.java))).then { throw ConflictError("There is already an URL with that vanity") }

        // Save the invalid URL
        shortUrlService!!.save(su.target!!, su.IP!!, vanity = su.id!!).block()!!
    }

    @Test
    open fun saveValidTemplate1() {
        // Generate valid URL with template
        val su = genURL("vanity/{0}", "http://vanity.com/{0}")

        // When we save an URL, return the same URL
        `when`(shortUrlRepository!!.save(any(ShortURL::class.java))).then { Mono.just<ShortURL>(it.getArgument(0)) }

        // Save a URL with the same template as the first one
        val other1 = shortUrlService!!.save("http://vanity.com/{template}", "127.0.0.1", vanity="vanity/{template}").block()!!

        // Check that the returned URL has the same vanity and url as the first URL
        assertEquals(su.id, other1.id)
        assertEquals(su.target, other1.target)
    }

    @Test(expected = BadRequestError::class)
    open fun saveInvalidTemplate1() {
        // Should return an exception, since the templates dont match
        shortUrlService!!.save("http://vanity.com/{template}", "127.0.0.1", vanity="vanity/{dontmatch}")
    }

    @Test(expected = BadRequestError::class)
    open fun saveInvalidTemplate2() {
        // Should return an exception, since the templates dont match
        shortUrlService!!.save("http://vanity.com/{template}", "127.0.0.1", vanity="vanity/{groupa}/{groupb}")
    }

    @Test(expected = BadRequestError::class)
    open fun saveInvalidTemplate3() {
        // Should return an exception, since the URLs dont match
        shortUrlService!!.save("http://vanity.com/{template}", "127.0.0.1", vanity="api/{template}")
    }
}