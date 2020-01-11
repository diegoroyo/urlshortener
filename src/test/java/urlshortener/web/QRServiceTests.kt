package urlshortener.web

import org.junit.Before
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
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
import org.junit.Assert.assertEquals
import urlshortener.repository.ShortURLRepository
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringJUnit4ClassRunner::class)
open class QRServiceTests {

    @Mock
    private val shortUrlRepository: ShortURLRepository? = null

    @InjectMocks
    private val shortUrlService: ShortURLService? = null

    private val exampleURL = "http://localhost:8080/34b53c20"

    private val exampleQrB64 : String = "iVBORw0KGgoAAAANSUhEUgAAAZAAAAGQAQAAAACoxAthAAABYElEQVR42u3bUW7DIAyAYaQczFfPwSx5yxrATpYR3Oyh0o+qiga+NwtjSItNtwKBQCAQCAQC+VCyltqWrS/fT5af50sYgkDyZP/pOnXOaQgCSZLXnO257mH4Cs4wBIE8QqxDMQjkX8hhxYNAHiG9o0V0C0irS984I0MgQ+LLhMPnTmUBgYyIb1spqp3fOSGBQIZkn1bLBLHYuVj6IJAEaXs2aUnWVaYQSJq03Vqf73KuhPiEQDKkHdKaC869r9e7PgjkNvFh2Q/T3Lf9GckQyJD0Rc9VphJD9JiRIZApUuoq10a1J9lWmUIgefLbjXmbLOH8FgJJkh5+bt1b4ztCEEiahGNbl2rX6/QKgUyRcMek8XgtcggkSeI1ejmXCefrJwhkkqynO3SL2VYgkAeJP/eIiyEE8j7x0RjqBQjkPRKs37NBII+Qw9/lNLz8bzqoLCCQIZlpEAgEAoFAIJCPI19aqvB+W73T0gAAAABJRU5ErkJggg=="

    @Test
    @Throws(Exception::class)
    open fun generateValidQR() {
        val respQr64 = shortUrlService!!.generateQR(exampleURL).block()!!
        assertEquals(respQr64, exampleQrB64)
    }
}