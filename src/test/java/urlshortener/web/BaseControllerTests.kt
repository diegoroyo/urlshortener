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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import reactor.core.publisher.Mono
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


open class BaseControllerTests {

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
    open fun saveValidURL() {
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
    open fun saveInvalidURL() {
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