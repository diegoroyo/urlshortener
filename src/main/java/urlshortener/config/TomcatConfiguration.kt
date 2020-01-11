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

package urlshortener.config

import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory

import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component


/**
 * Customize special characters in order to avoid exceptions of Tomcat server
 */
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