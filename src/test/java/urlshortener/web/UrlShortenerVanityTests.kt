package urlshortener.web

import org.junit.Before
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import reactor.core.publisher.Mono
import urlshortener.domain.ShortURL
import urlshortener.repository.ShortURLRepository
import urlshortener.service.ShortURLService
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import java.net.URI
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.junit.Assert.assertEquals
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import urlshortener.util.*
import urlshortener.exception.*
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.mockito.AdditionalAnswers
import org.mockito.Mockito
import org.junit.Ignore

@RunWith(SpringJUnit4ClassRunner::class)
open class UrlShortenerVanityTests {

    @Mock
    private val shortUrlRepository: ShortURLRepository? = null

    @InjectMocks
    private val shortUrlService: ShortURLService? = null

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @Test
    @Throws(Exception::class)
    open fun saveValidVanity() {
        // guardar en POST / (con vanity y sin template)
        // - codigo 201, respuesta correcta
        // ver que la redirección es correcta
        // - codigo 307, redirecciona a una url correcta
    }

    @Test
    @Throws(Exception::class)
    open fun saveInvalidVanity() {
        // POST / (con vanity, sin template) con URL de formato invalido
        // - todas devuelven código 400
        // - puede devolver codigo 409 (conflicto)
        // ver que la redirección es correcta
        // - código 404 ya que las URL son inválidas y no se guardan
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

    @Test
    @Ignore // TODO
    open fun saveValidTemplate2() {
        // url with same vanity and same target
        val su = genURL("vanity/{0}", "http://vanity.com/{0}")
        `when`(shortUrlRepository!!.save(any(ShortURL::class.java))).thenReturn(Mono.error<ShortURL>(ConflictError("")))
        val other2 = shortUrlService!!.save("http://vanity.com/{template}", "127.0.0.1", vanity="vanity/{template}").block()!!
        assertEquals(su.id, other2.id)
        assertEquals(su.target, other2.target)
    }

    @Test(expected = BadRequestError::class)
    @Ignore // TODO
    open fun saveValidTemplate3() {
        // url with same vanity and different target
        `when`(shortUrlRepository!!.save(any(ShortURL::class.java))).thenReturn(Mono.error<ShortURL>(ConflictError("")))
        shortUrlService!!.save("http://different.com/{template}", "127.0.0.1", vanity="vanity/{template}")
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