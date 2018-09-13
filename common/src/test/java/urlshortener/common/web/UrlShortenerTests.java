package urlshortener.common.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static urlshortener.common.web.fixture.ShortURLFixture.someUrl;

public class UrlShortenerTests {

    private MockMvc mockMvc;

    @Mock
    private ClickRepository clickRepository;

    @Mock
    private ShortURLRepository shortURLRepository;

    @InjectMocks
    private UrlShortenerController urlShortener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
    }

    @Test
    public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
            throws Exception {
        when(shortURLRepository.findByKey("someKey")).thenReturn(someUrl());
        when(clickRepository.save(any())).thenAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]);

        mockMvc.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isTemporaryRedirect())
                .andExpect(redirectedUrl("http://example.com/"));
    }

    @Test
    public void thatRedirecToReturnsNotFoundIdIfKeyDoesNotExist()
            throws Exception {
        when(shortURLRepository.findByKey("someKey")).thenReturn(null);

        mockMvc.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void thatShortenerCreatesARedirectIfTheURLisOK() throws Exception {
        configureTransparentSave();

        mockMvc.perform(post("/link").param("url", "http://example.com/"))
                .andDo(print())
                .andExpect(redirectedUrl("http://localhost/f684a3c4"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hash", is("f684a3c4")))
                .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
                .andExpect(jsonPath("$.target", is("http://example.com/")))
                .andExpect(jsonPath("$.sponsor", is(nullValue())));
    }

    @Test
    public void thatShortenerCreatesARedirectWithSponsor() throws Exception {
        configureTransparentSave();

        mockMvc.perform(
                post("/link").param("url", "http://example.com/").param(
                        "sponsor", "http://sponsor.com/")).andDo(print())
                .andExpect(redirectedUrl("http://localhost/f684a3c4"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hash", is("f684a3c4")))
                .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
                .andExpect(jsonPath("$.target", is("http://example.com/")))
                .andExpect(jsonPath("$.sponsor", is("http://sponsor.com/")));
    }

    @Test
    public void thatShortenerFailsIfTheURLisWrong() throws Exception {
        configureTransparentSave();

        mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void thatShortenerFailsIfTheRepositoryReturnsNull() throws Exception {
        when(shortURLRepository.save(any(ShortURL.class)))
                .thenReturn(null);

        mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
                .andExpect(status().isBadRequest());
    }

    private void configureTransparentSave() {
        when(shortURLRepository.save(any(ShortURL.class)))
                .then((Answer<ShortURL>) invocation -> (ShortURL) invocation.getArguments()[0]);
    }
}
