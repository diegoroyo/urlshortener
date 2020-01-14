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

package urlshortener.service

import com.google.common.hash.Hashing
import java.nio.charset.StandardCharsets
import java.sql.Date
import org.springframework.http.HttpStatus
import urlshortener.domain.ShortURL

class ShortURLBuilder {

    // Id of the url
    private var id: String? = null

    // Target of the url
    private var target: String? = null

    // Date where url was shorted
    private var created: Date? = null

    // Mode
    private var mode: Int? = null

    // Control if the url is or not is active
    private var active: Boolean = false

    // Control if the url is or not is spam
    private var safe: Boolean? = null
    // Ip
    private var ip: String? = null

    /**
     * Constructor
     */
    fun build() = ShortURL(
        id,
        target,
        created,
        mode,
        active,
        safe,
        ip
    )



    /**
     * @param url is the id of the url
     * @param vanity is the vanity of the url
     * @return the short url with the shorId stored
     */
    fun target(url: String, vanity: String? = null): ShortURLBuilder {
        this.target = url
        if (vanity.isNullOrBlank()) {
            this.id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString()
        } else {
            this.id = vanity
        }
        return this
    }



    /**
     * @param vanity
     * @return the short url with the date stored
     */
    fun createdNow(): ShortURLBuilder {
        this.created = Date(System.currentTimeMillis())
        return this
    }



    /**
     * @return the short url with the mode temporary redirect
     */
    fun temporaryRedirect(): ShortURLBuilder {
        this.mode = HttpStatus.TEMPORARY_REDIRECT.value()
        return this
    }


    /**
     * @return the short url actived by defect
     */
    fun makeActive(): ShortURLBuilder {
        this.active = true
        return this
    }


    /**
     * @return the short url safe by defect
     */
    fun treatAsSafe(): ShortURLBuilder {
        this.safe = true
        return this
    }


    /**
     * @param ip
     * @return the short url with the ip stored
     */
    fun ip(ip: String): ShortURLBuilder {
        this.ip = ip
        return this
    }
}
