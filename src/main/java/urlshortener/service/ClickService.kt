package urlshortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import urlshortener.domain.Click;
import urlshortener.repository.ClickRepository;

import java.sql.Date;

@Service
public class ClickService(private val clickRepository: ClickRepository) {

    private val log: Logger = LoggerFactory.getLogger(ClickService::class.java);

    public fun saveClick(shortId: String, ip: String) {
        var cl: Click? = ClickBuilder().shortId(shortId).createdNow().ip(ip).build();
        cl = clickRepository.save(cl!!);
        // TODO check why cl.getId() doesnt work
        log.info(if (cl != null) "[" + shortId + "] saved with id [ -- no id -- ]" else "[" + shortId + "] was not saved");
    }

}
