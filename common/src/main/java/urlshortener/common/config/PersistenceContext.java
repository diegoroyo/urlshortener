package urlshortener.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "urlshortener.common.repository" })
public class PersistenceContext {

}
