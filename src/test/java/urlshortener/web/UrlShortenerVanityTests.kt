package urlshortener.web

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import reactor.core.publisher.Mono
import urlshortener.domain.ShortURL
import urlshortener.web.UrlShortenerController
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
open class UrlShortenerVanityTests {

    @Mock
    private val shortUrlRepository: ShortURLRepository? = null

    @Mock
    private val shortUrlController: UrlShortenerController? = null

    @InjectMocks
    private val shortUrlService: ShortURLService? = null

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @Test
    @Throws(Exception::class)
    open fun saveValidVanity() {
        // Save valid URL with vanity
        val su = exampleURL()
        `when`(shortUrlRepository!!.save(any(ShortURL::class.java))).then { Mono.just<ShortURL>(it.getArgument(0)) }
        val other1 = shortUrlService!!.save(su.target!!, su.IP!!, vanity=su.id!!).block()!!
        assertEquals(su.id, other1.id)
        assertEquals(su.target, other1.target)
    }

    @Test(expected=ConflictError::class)
    @Throws(Exception::class)
    open fun saveInvalidVanity() {
        // Save invalid URL with vanity
        val su = exampleURL()

        // When the URL is saved, it should return an exception
        `when`(shortUrlRepository!!.save(any(ShortURL::class.java))).then { throw ConflictError("There is already an URL with that vanity") }

        // Save the invalid URL
        shortUrlService!!.save(su.target!!, su.IP!!, vanity = su.id!!).block()!!
    }

    @Test
    open fun saveValidTemplate1() {
        // Save valid URL with template
        val su = genURL("vanity/{0}", "http://vanity.com/{0}")
        `when`(shortUrlRepository!!.save(any(ShortURL::class.java))).then { Mono.just<ShortURL>(it.getArgument(0)) }
        val other1 = shortUrlService!!.save("http://vanity.com/{template}", "127.0.0.1", vanity="vanity/{template}").block()!!
        assertEquals(su.id, other1.id)
        assertEquals(su.target, other1.target)
    }

    @Test(expected = BadRequestError::class)
    open fun saveInvalidTemplate1() {
        shortUrlService!!.save("http://vanity.com/{template}", "127.0.0.1", vanity="vanity/{dontmatch}")
    }

    @Test(expected = BadRequestError::class)
    open fun saveInvalidTemplate2() {
        shortUrlService!!.save("http://vanity.com/{template}", "127.0.0.1", vanity="vanity/{groupa}/{groupb}")
    }

    @Test(expected = BadRequestError::class)
    open fun saveInvalidTemplate3() {
        shortUrlService!!.save("http://vanity.com/{template}", "127.0.0.1", vanity="api/{template}")
    }
}