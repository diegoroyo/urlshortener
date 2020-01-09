package urlshortener.util

import urlshortener.domain.ShortURL
import urlshortener.domain.Click

fun exampleURL(): ShortURL {
    return ShortURL(id="someKey", target="http://example.com/", created = null, mode = 307, active = true,
            safe = true, IP = "127.0.0.1")
}

fun exampleClick(): Click {
    return Click(1, "http://example.com", null, "referer", "firefox", "linux")
}
    
fun genURL(id: String, target: String): ShortURL {
    return ShortURL(id=id, target=target, created = null, mode = 307, active = false,
            safe = null, IP = null)
}