package urlshortener.web

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import reactor.core.publisher.Mono
import urlshortener.domain.Click
import urlshortener.domain.ShortURL
import urlshortener.service.ClickService
import urlshortener.service.ShortURLService


open class StatisticsServiceTests {

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
    open fun testValidStatistics() {
        // crear url y darle algunos clicks
        // peticion a /manage/statistics
        // - codigo 200, clicks correctos etc
        val url = exampleURL()
        `when`(shortUrlService!!.findByKey(url.id!!)).thenReturn(Mono.just(url))

        var clicks = 0

        doAnswer {
            clicks++
        }
                .`when`<ClickService>(clickService).saveClick(url.id!!,url.IP!!, "referer", "firefox", "linux")

        for (i in 1..3) {
            mockMvc!!.perform(get("/{id}", url.id)).andDo(print())
                    .andExpect(status().isTemporaryRedirect)
                    .andExpect(redirectedUrl(url.target!!))
        }
        assertEquals(clicks, 3)
    }

    @Test
    @Throws(Exception::class)
    open fun testInvalidStatistics() {
        // peticion a /manage/statistics con url incorrecta
        // - codigo 400
    }

    // TODO: impor from a common class?
    open fun exampleURL(): ShortURL {
        return ShortURL(id="someKey", target="http://example.com/", created = null, mode = 307, active = true,
                safe = true, IP = "127.0.0.1")
    }
}