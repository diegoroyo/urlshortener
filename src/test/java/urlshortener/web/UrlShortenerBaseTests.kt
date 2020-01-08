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
import urlshortener.service.ShortURLService
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.junit.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


open class UrlShortenerBaseTests {

    private var mockMvc: MockMvc? = null

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
    // TODO quitar este test cuando terminemos
    open fun redireccionExito() {
        `when`(shortUrlService!!.findByKey("someKey")).thenReturn(Mono.just(exampleURL()))
        mockMvc!!.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isTemporaryRedirect)
                .andExpect(redirectedUrl("http://example.com/"))
    }

    @Test
    @Throws(Exception::class)
    open fun saveValidURL() {
        // guardar en POST / (sin vanity)
        // - codigo 201, respuesta correcta
        // ver que la redirección es correcta
        // - codigo 307, redirecciona a una url correcta

        // Creates a random URL
        val url = exampleURL()

        // When the save function is called with the created URLs parameters, return the URL
        `when`(shortUrlService!!.save(url.target!!, url.IP!!)).thenReturn(Mono.just(url))

        // Create a POST request with the target of the created URL and check that it returns the status 201
        mockMvc!!.perform(post("/manage/link").param("url", url.target)).andDo(print())
                .andExpect(status().isCreated)
    }

    @Test
    @Throws(Exception::class)
    open fun saveInvalidURL() {
        // POST / (sin vanity) con varias URL con formato inválido
        // - todas devuelven código 400
        // ver que la redirección es correcta
        // - código 404 ya que las URL son inválidas y no se guardan

        // Creates a random URL
        val url = exampleURL()

        // When the save function is called with the created URLs parameters, return the URL
        `when`(shortUrlService!!.save(url.target!!, url.IP!!)).thenReturn(null)

        // Create a POST request with the target of the created URL and check that it returns the status 201
        mockMvc!!.perform(post("/manage/link").param("url", url.target)).andDo(print())
                .andExpect(status().isCreated)
    }

    open fun exampleURL(): ShortURL {
        return ShortURL(id="someKey", target="http://example.com/", created = null, mode = 307, active = true,
                safe = true, IP = "127.0.0.1")
    }
}