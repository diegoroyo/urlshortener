package urlshortener.domain

import java.sql.Date
import javax.validation.constraints.Size
import java.io.Serializable

data class ShortURL (
    @Size(max = 256)
    // Hash or vanity
    var id: String? = null,      
    @Size(max = 1024)
    // Original uri
    var target: String? = null,  
    // Date
    var created: Date? = null,   
    // MOde of  redirection (e.g. 307 temporary redirect)
    var mode: Int? = null,       
    // if is active it's accesible 
    var active: Boolean = false, 
    // Malicious uri
    var safe: Boolean? = null,   
    @Size(max = 20)
    // Ip of user creator
    var IP: String? = null       
) : Serializable
