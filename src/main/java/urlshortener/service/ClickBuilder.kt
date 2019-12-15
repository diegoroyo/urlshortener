package urlshortener.service

import java.sql.Date
import urlshortener.domain.Click

class ClickBuilder {

    private var shortId: String? = null
    private var created: Date? = null
    private var referer: String? = null
    private var browser: String? = null
    private var platform: String? = null
    private var ip: String? = null

    fun build() = Click(
        null,
        shortId,
        created,
        referer,
        browser,
        platform,
        ip
    )

    fun shortId(shortId: String): ClickBuilder {
        this.shortId = shortId
        return this
    }

    fun createdNow(): ClickBuilder {
        this.created = Date(System.currentTimeMillis())
        return this
    }

    fun referer(referer: String?): ClickBuilder {
        this.referer = referer
        return this
    }

    fun browser(browser: String?): ClickBuilder {
        this.browser = browser
        return this
    }

    fun platform(platform: String?): ClickBuilder {
        this.platform = platform
        return this
    }

    fun ip(ip: String?): ClickBuilder {
        this.ip = ip
        return this
    }
}
