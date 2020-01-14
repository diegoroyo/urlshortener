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
import org.junit.Assert.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.util.ReflectionTestUtils
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.TestPropertySource
import org.mockito.AdditionalAnswers
import org.mockito.Mockito
import urlshortener.util.*
import org.junit.Ignore

@RunWith(SpringJUnit4ClassRunner::class)
@TestPropertySource(locations = arrayOf("/application.properties"))
open class SaveServiceTests {
    
    private val VALID_URL = "https://example.com"
    private val MALWARE_URL = "https://testsafebrowsing.appspot.com/s/malware.html"

    @Mock
    private val shortUrlRepository: ShortURLRepository? = null

    @InjectMocks
    private val shortUrlService: ShortURLService? = null

    @Value("\${google.safebrowsing.api_key}")
    var safeBrowsingKey: String? = null

    @Before
    open fun setup() {
        ReflectionTestUtils.setField(shortUrlService!!, "safeBrowsingKey", safeBrowsingKey);
    }

    @Test
    @Ignore // Evitar demasiadas peticiones a la API
    open fun checkSafeURL() {
        val su = genURL("valid", VALID_URL)
        // Check service is able to detect safe url
        val ret = shortUrlService!!.safeBrowsing(su)
        assertEquals(ret.active, true)
        assertEquals(ret.safe, true)
    }

    @Test
    @Ignore // Evitar demasiadas peticiones a la API
    open fun checkUnsafeURL() {
        val su = genURL("malware", MALWARE_URL)
        // Check service is able to detect safe url
        val ret = shortUrlService!!.safeBrowsing(su)
        assertEquals(ret.active, false)
        assertEquals(ret.safe, false)
    }
}