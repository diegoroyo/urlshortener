package urlshortener.service;

import urlshortener.domain.Click;

import java.sql.Date;

public class ClickBuilder {

    private var shortId: String? = null;
    private var created: Date? = null;
    private var referrer: String? = null;
    private var browser: String? = null;
    private var platform: String? = null;
    private var ip: String? = null;
    private var country: String? = null;

    public fun build() = Click(
        null,
        shortId,
        created,
        referrer,
        browser,
        platform,
        ip
    );

    public fun shortId(shortId: String) : ClickBuilder {
        this.shortId = shortId;
        return this;
    }

    public fun createdNow() : ClickBuilder {
        this.created = Date(System.currentTimeMillis());
        return this;
    }

    public fun referrer(referer: String) : ClickBuilder {
        this.referrer = referer;
        return this;
    }

    public fun browser(browser: String?) : ClickBuilder {
        this.browser = browser;
        return this;
    }

    public fun platform(platform: String?) : ClickBuilder {
        this.platform = platform;
        return this;
    }

    public fun ip(ip: String) : ClickBuilder {
        this.ip = ip;
        return this;
    }

}
