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

package urlshortener.util

import urlshortener.domain.ShortURL
import urlshortener.domain.Click


/**
 * Creates a sample shortened url
 */
fun exampleURL(): ShortURL {
    return ShortURL(id="someKey", target="http://example.com/", created = null, mode = 307, active = true,
            safe = true, IP = "127.0.0.1")
}

/**
 * Creates a sample click
 */
fun exampleClick(): Click {
    return Click(1, "http://example.com", null, "referer", "firefox", "linux")
}

/**
 * Generates a url
 * @param id Identifier of the url
 * @param target Target of the url
 * @return Generated url
 */
fun genURL(id: String?, target: String): ShortURL {
    return ShortURL(id=id, target=target, created = null, mode = 307, active = false,
            safe = null, IP = "127.0.0.1")
}