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

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


import springfox.documentation.swagger2.annotations.EnableSwagger2
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket


@Configuration
@EnableSwagger2
class SwaggerConfiguration {

    /**
     * Run the swagger service in order to document the app
     */
    @Bean
    open fun api(): Docket = Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build()
}







