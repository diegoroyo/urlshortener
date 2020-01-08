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


open class UrlShortenerTests {

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
    open fun thatRedirectToReturnsTemporaryRedirectIfKeyExists() {
        `when`(shortUrlService!!.findByKey("someKey")).thenReturn(Mono.just(someUrl()))
        mockMvc!!.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isTemporaryRedirect)
                .andExpect(redirectedUrl("http://example.com/"))
    }

    open fun someUrl(): ShortURL {
        return ShortURL(id="someKey", target="http://example.com/", created = null, mode = 307, active = true,
                safe = true, IP = null)
    }
}