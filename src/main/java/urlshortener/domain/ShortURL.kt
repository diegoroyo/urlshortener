package urlshortener.domain

import java.net.URI
import java.sql.Date
import javax.validation.constraints.Size
import org.springframework.http.HttpStatus
import java.lang.RuntimeException
import org.springframework.web.bind.annotation.ResponseStatus

data class ShortURL(
    @Size(max = 256)
    var id: String? = null,      // hash o vanity
    @Size(max = 1024)
    var target: String? = null,  // uri original
    var created: Date? = null,   // fecha de creación
    var mode: Int? = null,       // modo de redirección (e.g. 307 temporary redirect)
    var active: Boolean = false, // si está activa, se puede acceder a ella
    var safe: Boolean? = null,   // maliciosa o no
    @Size(max = 20)
    var IP: String? = null       // ip del usuario creador
)

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
object ShortURLStorageError : RuntimeException("Internal storage error")

@ResponseStatus(HttpStatus.NOT_FOUND)
object ShortURLNotFound : RuntimeException("URL not found")

@ResponseStatus(HttpStatus.BAD_REQUEST)
object ShortURLInvalid : RuntimeException("Invalid URL")

@ResponseStatus(HttpStatus.CONFLICT)
object ShortURLConflicting : RuntimeException("Conflicting URL")