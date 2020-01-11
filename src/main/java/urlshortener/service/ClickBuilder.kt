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

import java.sql.Date
import urlshortener.domain.Click


class ClickBuilder {

    // Id of the url clicked
    private var shortId: String? = null
    // Date when the url is accesed
    private var created: Date? = null
    // Posible referer of the url
    private var referer: String? = null
    // client broswer where the url is accesed
    private var browser: String? = null
    // Operating system
    private var platform: String? = null
    // Ip
    private var ip: String? = null

    /*
     * Constructor 
     */
    fun build() = Click(
        null,
        shortId,
        created,
        referer,
        browser,
        platform,
        ip
    )



    /*
     * @param shortId is the id of the url
     * @return the click with the shorId stored
     */
    fun shortId(shortId: String): ClickBuilder {
        this.shortId = shortId
        return this
    }



    /*
     * @param shortId is the id of the url
     * @return the click with the shorId stored
     */
    fun createdNow(): ClickBuilder {
        this.created = Date(System.currentTimeMillis())
        return this
    }



    /*
     * @param referer is the id of the url
     * @return the click with the referer stored
     */
    fun referer(referer: String?): ClickBuilder {
        this.referer = referer
        return this
    }



    /*
     * @param browser is the id of the url
     * @return the click with the browser stored
     */
    fun browser(browser: String?): ClickBuilder {
        this.browser = browser
        return this
    }



    /*
     * @param platform is the id of the url
     * @return the click with the platform stored
     */
    fun platform(platform: String?): ClickBuilder {
        this.platform = platform
        return this
    }


    
    /*
     * @param ip is the id of the url
     * @return the click with the ip stored
     */
    fun ip(ip: String?): ClickBuilder {
        this.ip = ip
        return this
    }
}
