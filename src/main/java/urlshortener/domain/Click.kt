package urlshortener.domain

import java.sql.Date
import javax.validation.constraints.Size

data class Click(
    var clickId: Int? = null,
    @Size(max = 256)
    var shortId: String? = null,
    var created: Date? = null,
    @Size(max = 1024)
    var referer: String? = null,
    @Size(max = 50)
    var browser: String? = null,
    @Size(max = 50)
    var platform: String? = null,
    @Size(max = 20)
    var ip: String? = null
)
