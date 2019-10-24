package urlshortener.service;

import org.springframework.stereotype.Service;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import urlshortener.web.UrlShortenerController;

@Service
public class ShortURLService(private val shortURLRepository: ShortURLRepository) {

    public fun findByKey(id: String) : ShortURL = shortURLRepository.findByKey(id);

    public fun save(url: String, ip: String) : ShortURL {
        val su: ShortURL = ShortURLBuilder()
                .target(url)
                .createdNow()
                .temporaryRedirect()
                .ip(ip)
                .unknownCountry() // TODO
                .build();
        return shortURLRepository.save(su);
    }
}
