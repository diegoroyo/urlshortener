package urlshortener.domain

import java.net.URI;
import java.sql.Date;

data class ShortURL(
    var hash: String? = null,
    var target: String? = null,
    var uri: URI? = null,
    var sponsor: String? = null,
    var created: Date? = null,
    var owner: String? = null,
    var mode: Int? = null,
    var safe: Boolean? = null,
    var IP: String? = null,
    var country: String? = null
)