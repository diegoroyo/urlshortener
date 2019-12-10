package urlshortener.config

import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory

import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component


@Component
class TomcatConfiguration : WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    override fun customize(factory: TomcatServletWebServerFactory) {
        factory.addConnectorCustomizers(TomcatConnectorCustomizer { connector ->
            run {
                connector.setAttribute("relaxedPathChars", "<>[\\]^`{|}")
                connector.setAttribute("relaxedQueryChars", "<>[\\]^`{|}")
            }
        })
    }
}