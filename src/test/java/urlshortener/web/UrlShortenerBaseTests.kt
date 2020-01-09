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
import urlshortener.exception.BadRequestError
import urlshortener.exception.ConflictError
import urlshortener.exception.NotFoundError
import urlshortener.service.ClickService
import urlshortener.util.*


open class UrlShortenerBaseTests {

    private var mockMvc: MockMvc? = null

    @Mock
    private val shortUrlService: ShortURLService? = null

    @Mock
    private val clickService: ClickService? = null

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

        // Create a random URL
        val url = exampleURL()

        // When the find function is called return the example URL
        `when`(shortUrlService!!.findByKey(url.id!!)).thenReturn(Mono.just(url))
        mockMvc!!.perform(get("/{id}", url.id)).andDo(print())
                .andExpect(status().isTemporaryRedirect)
                .andExpect(redirectedUrl(url.target!!))
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

        // When the find function is called return the example URL
        `when`(shortUrlService.findByKey(url.id!!)).thenReturn(Mono.just(exampleURL()))

        // Create a GET request with the created link and check it returns 307 status
        mockMvc!!.perform(get("/{id}", url.id)).andDo(print())
                .andExpect(status().isTemporaryRedirect)
                .andExpect(redirectedUrl(url.target!!))
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

        // When the save function is called with the created URLs parameters, throw first a BadRequestError exception
        // and after a ConflictError exception
        `when`(shortUrlService!!.save(url.target!!, url.IP!!)).thenThrow(BadRequestError("The URL is incorrect"))
                .thenThrow(ConflictError("There is already a URL with that vanity"))

        // Create a POST request with the target of the created URL and check that it returns the status 201
        mockMvc!!.perform(post("/manage/link").param("url", url.target)).andDo(print())
                .andExpect(status().isBadRequest)

        // Create a POST request with the target of the created URL and check that it returns the status 201
        mockMvc!!.perform(post("/manage/link").param("url", url.target)).andDo(print())
                .andExpect(status().isConflict)

        // When the find function is called throw a NotFoundErrorException
        `when`(shortUrlService.findByKey(url.id!!)).thenThrow(NotFoundError("There is no URL with the given identifier"))

        // Create a GET request with the created link and check it returns 404 status
        mockMvc!!.perform(get("/{id}", url.id)).andDo(print())
                .andExpect(status().isNotFound)
    }
}