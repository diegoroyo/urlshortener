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


open class UrlShortenerVanityTests {

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
    @Throws(Exception::class)
    open fun saveValidTemplate() {
        // guardar en POST / (con vanity y con template)
        // - codigo 201, respuesta correcta
        // ver que la redirección es correcta
        // - codigo 307, redirecciona a una url correcta
    }

    @Test
    @Throws(Exception::class)
    open fun saveInvalidTemplate() {
        // POST / (con vanity, con template) con URL de formato invalido
        // - todas devuelven código 400
        //   - no coinciden los { braces }
        //   - formato invalido
        //   - etc
        // - puede devolver codigo 409 (conflicto)
        // ver que la redirección es correcta
        // - código 404 ya que las URL son inválidas y no se guardan
    }
}