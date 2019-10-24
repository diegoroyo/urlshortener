package urlshortener.domain

import java.net.URI;
import java.sql.Date;

data class ShortURL(
    var id: String? = null,      // hash o vanity
    var target: String? = null,  // uri original
    var uri: URI? = null,        // uri acortada -> original
    var created: Date? = null,   // fecha de creación
    var mode: Int? = null,       // modo de redirección (e.g. 307 temporary redirect)
    var active: Boolean = false, // si está activa, se puede acceder a ella
    var safe: Boolean? = null,   // maliciosa o no
    var IP: String? = null,      // ip del usuario creador
    var country: String? = null  // país del usuario creador
)