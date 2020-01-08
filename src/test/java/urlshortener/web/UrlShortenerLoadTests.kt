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
import urlshortener.service.ClickService
import urlshortener.service.ShortURLService
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import java.net.URI
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


open class UrlShortenerLoadTests {

    private var mockMvc: MockMvc? = null

    @Mock
    private val clickService: ClickService? = null

    @Mock
    private val shortUrlService: ShortURLService? = null

    @InjectMocks
    private val urlShortener: UrlShortenerController? = null

    @Before
    open fun setup() {
        MockitoAnnotations.initMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build()
    }

    @Test
    @Throws(Exception::class)
    open fun loadTestShorten() {
        // varias peticiones a POST /, al cabo de un tiempo devuelve 429
        // hacer peticion desde otra ip (si es posible) y ver que se puede
    }

    @Test
    @Throws(Exception::class)
    open fun loadTestRedirection() {
        // varias peticiones a GET /{id}, al cabo de un tiempo devuelve 429
        // hacer peticion desde otra ip (si es posible) y ver que se puede
    }
    
    @Test
    @Throws(Exception::class)
    open fun loadTestQR() {
        // varias peticiones a GET /manage/qr, al cabo de un tiempo devuelve 429
        // hacer peticion desde otra ip (si es posible) y ver que se puede
    }

    @Test
    @Throws(Exception::class)
    open fun loadTestStatistics() {
        // varias peticiones a GET /manage/qr, al cabo de un tiempo devuelve 429
        // hacer peticion desde otra ip (si es posible) y ver que se puede
    }

}