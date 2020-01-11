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


package urlshortener.domain

import java.sql.Date
import javax.validation.constraints.Size


/**
 * Class that represents an object click
 */
data class Click(
    // Id
    var clickId: Int? = null,
    @Size(max = 256)
    // Id of the short url referenced
    var shortId: String? = null,
    // Date
    var created: Date? = null,
    @Size(max = 1024)
    // Referer
    var referer: String? = null,
    @Size(max = 50)
    // Client brower
    var browser: String? = null,
    @Size(max = 50)
    // Operating systems
    var platform: String? = null,
    @Size(max = 20)
    // Ip
    var ip: String? = null
)
