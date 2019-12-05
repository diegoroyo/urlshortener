package urlshortener.domain

import org.springframework.web.server.ResponseStatusException
import java.sql.Date
import javax.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

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

// TODO handling

object ClickStorageError : ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal storage error")

@ResponseStatus(HttpStatus.NOT_FOUND)
object ClickNotFound : RuntimeException("Click not found")
