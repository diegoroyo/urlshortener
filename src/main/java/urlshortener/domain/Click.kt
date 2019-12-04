package urlshortener.domain

import org.springframework.web.server.ResponseStatusException
import java.sql.Date
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

data class Click(
    var clickId: Int? = null,
    var shortId: String? = null,
    var created: Date? = null,
    var referrer: String? = null,
    var browser: String? = null,
    var platform: String? = null,
    var ip: String? = null
)

// TODO handling

object ClickStorageError : ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal storage error")

@ResponseStatus(HttpStatus.NOT_FOUND)
object ClickNotFound : RuntimeException("Click not found")
