package urlshortener.service;

import urlshortener.domain.Click;

import java.sql.Date;

public class ClickBuilder {

    private var hash: String? = null;
    private var created: Date? = null;
    private var referrer: String? = null;
    private var browser: String? = null;
    private var platform: String? = null;
    private var ip: String? = null;
    private var country: String? = null;

    public fun build() = Click(
        null,
        hash,
        created,
        referrer,
        browser,
        platform,
        ip,
        country
    );

    public fun hash(hash: String) : ClickBuilder {
        this.hash = hash;
        return this;
    }

    public fun createdNow() : ClickBuilder {
        this.created = Date(System.currentTimeMillis());
        return this;
    }

    public fun noReferrer() : ClickBuilder {
        this.referrer = null;
        return this;
    }

    public fun unknownBrowser() : ClickBuilder {
        this.browser = null;
        return this;
    }

    public fun unknownPlatform() : ClickBuilder {
        this.platform = null;
        return this;
    }


    public fun ip(ip: String) : ClickBuilder {
        this.ip = ip;
        return this;
    }

    public fun withoutCountry() : ClickBuilder {
        this.country = null;
        return this;
    }

}
