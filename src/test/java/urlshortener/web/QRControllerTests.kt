/*
 *******************************************
 *** Urlshortener - Web Engineering ********
 *** Authors: Name  ************************
 *** Andrew Mackay - 737069 ****************
 *** Ruben Rodríguez Esteban - 737215 ******
 *** Diego Royo Meneses - 740388 ***********
 *** Course: 2019 - 2020 *******************
 *******************************************
 */

package urlshortener.web

import org.junit.Before
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import reactor.core.publisher.Mono
import urlshortener.service.ClickService
import urlshortener.service.ShortURLService
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.junit.Test
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.util.ReflectionTestUtils
import urlshortener.util.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch


open class QRControllerTests {

    private var mockMvc: MockMvc? = null

    @InjectMocks
    private val urlShortener: UrlShortenerController? = null

    @Mock
    private val clickService: ClickService? = null

    @Mock
    private val shortUrlService: ShortURLService? = null

    private val base = "http://localhost:8080/"

    private val exampleID = "34b53c20"

    private val exampleQrB64 = "iVBORw0KGgoAAAANSUhEUgAAAZAAAAGQAQAAAACoxAthAAABYElEQVR42u3bUW7DIAyAYaQczFfPwSx5yxrATpYR3Oyh0o+qiga+NwtjSItNtwKBQCAQCAQC+VCyltqWrS/fT5af50sYgkDyZP/pOnXOaQgCSZLXnO257mH4Cs4wBIE8QqxDMQjkX8hhxYNAHiG9o0V0C0irS984I0MgQ+LLhMPnTmUBgYyIb1spqp3fOSGBQIZkn1bLBLHYuVj6IJAEaXs2aUnWVaYQSJq03Vqf73KuhPiEQDKkHdKaC869r9e7PgjkNvFh2Q/T3Lf9GckQyJD0Rc9VphJD9JiRIZApUuoq10a1J9lWmUIgefLbjXmbLOH8FgJJkh5+bt1b4ztCEEiahGNbl2rX6/QKgUyRcMek8XgtcggkSeI1ejmXCefrJwhkkqynO3SL2VYgkAeJP/eIiyEE8j7x0RjqBQjkPRKs37NBII+Qw9/lNLz8bzqoLCCQIZlpEAgEAoFAIJCPI19aqvB+W73T0gAAAABJRU5ErkJggg=="

    private val badURL = "abc"
    
    @Before
    open fun setup() {
        MockitoAnnotations.initMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build()
        ReflectionTestUtils.setField(urlShortener!!, "serverIp", "localhost")
        ReflectionTestUtils.setField(urlShortener, "serverPort", "8080")
    }
        
    @Test
    open fun generateValidQRController() {
        // do async petition
        `when`(shortUrlService!!.generateQR("$base$exampleID")).thenReturn(Mono.just(exampleQrB64))
        `when`(shortUrlService.findByKey(exampleID)).thenReturn(Mono.just(genURL(exampleID, "http://www.unizar.es"))) // no importa
        val mvcResult = mockMvc!!.perform(get("/manage/qr").param("url", "$base$exampleID"))
                                      .andDo(print())
                                      .andExpect(status().isOk)
                                      .andReturn()

        // wait for async result
        mockMvc!!.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk)
                    .andExpect(content().contentTypeCompatibleWith("text/plain"))
                    .andExpect(content().string(exampleQrB64))
    }
    
    @Test
    open fun generateInvalidQR() {
         mockMvc!!.perform(get("/manage/qr").param("url", badURL))
                  .andDo(print())
                  .andExpect(status().isBadRequest)
    }
}