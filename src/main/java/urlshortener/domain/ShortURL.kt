package urlshortener.domain

import java.sql.Date
import javax.validation.constraints.Size

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
