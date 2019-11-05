package urlshortener.domain

import java.sql.Date;

data class Click(
    var clickId: Long? = null,
    var shortId: String? = null,
    var created: Date? = null,
    var referrer: String? = null,
    var browser: String? = null,
    var platform: String? = null,
    var ip: String? = null,
    var country: String? = null
)