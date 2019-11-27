package urlshortener.domain

import java.lang.RuntimeException
import java.sql.Date
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

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

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
object ClickStorageError : RuntimeException("Internal storage error")

@ResponseStatus(HttpStatus.NOT_FOUND)
object ClickNotFound : RuntimeException("Click not found")
