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


open class SafeBrowsingTests {

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
    open fun checkSafeURL() {
        // llamar a la funcion de safe browsing con url normal
        // - devuelve que la url es segura
        // - active == true
    }

    @Test
    @Throws(Exception::class)
    open fun checkUnsafeURL() {
        // llamar a la funcion de safe browsing con
        // varias url de https://testsafebrowsing.appspot.com/)
        // - devuelve que son inseguras por la razon indicada
        // - active == false
    }
    
    @Test
    @Throws(Exception::class)
    open fun testQRcache() {
        // peticion a la funcion de safe browsing
        // - funciona bien, tarda un tiempo
        // peticion a la funcion de safe browsing
        // - resultado cacheado, rapido
    }

    @Test
    @Throws(Exception::class)
    open fun checkBatchProcess() {
        // crear url segura
        // modificar bd para que el link apunte a un sitio spam (https://testsafebrowsing.appspot.com/)
        // esperar un tiempo
        // la url debera tener el active == false
    }
}