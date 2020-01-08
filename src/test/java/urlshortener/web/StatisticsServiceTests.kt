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
import reactor.core.publisher.Flux
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

        // Obtain an example URL
        val url = exampleURL()

        // Return the example URL instead of loading it from the database
        `when`(shortUrlService!!.findByKey(url.id!!)).thenReturn(Mono.just(url))

        // Make 3 GET requests to the URL
        for (i in 1..3) {
            mockMvc!!.perform(get("/{id}", url.id)).andDo(print())
                    .andExpect(status().isTemporaryRedirect)
                    .andExpect(redirectedUrl(url.target!!))
        }

        // Obtain the invocations of the ClickService service
        val invocations = mockingDetails(clickService).invocations

        // Compare the number of invocations to the number of requests
        // TODO: specific invocations?
        assertEquals(3, invocations.size)

        // Return the example Click flux instead of loading it from the database
        `when`(clickService!!.getClicksFromURL(url.target!!, 1, 1)).thenReturn(Flux.just(exampleClick()))

        // Make a GET request to the statistics endpoint and expect a 200 status
        mockMvc!!.perform(get("/manage/statistics")
                .param("short", url.target)
                .param("pageNumber", "1")
                .param("pageSize", "1")).andDo(print())
                .andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    open fun testInvalidStatistics() {
        // peticion a /manage/statistics con url incorrecta
        // - codigo 400

        // Obtain an example URL
        val url = exampleURL()

        // Return the example Click flux instead of loading it from the database
        `when`(clickService!!.getClicksFromURL(url.target!!, 1, 1)).thenReturn(Flux.just(exampleClick()))

        // Make 3 GET request with missing parameters and expect 400 status
        mockMvc!!.perform(get("/manage/statistics")
                .param("short", url.target)
                .param("pageNumber", "1")).andDo(print())
                .andExpect(status().isBadRequest)

        mockMvc!!.perform(get("/manage/statistics")
                .param("short", url.target)
                .param("pageSize", "1")).andDo(print())
                .andExpect(status().isBadRequest)

        mockMvc!!.perform(get("/manage/statistics")
                .param("pageSize", url.target)
                .param("pageNumber", "1")).andDo(print())
                .andExpect(status().isBadRequest)
    }

    // TODO: impor from a common class?
    open fun exampleURL(): ShortURL {
        return ShortURL(id="someKey", target="http://example.com/", created = null, mode = 307, active = true,
                safe = true, IP = "127.0.0.1")
    }

    open fun exampleClick(): Click {
        return Click(1, "http://example.com", null, "referer", "firefox", "linux")
    }
}