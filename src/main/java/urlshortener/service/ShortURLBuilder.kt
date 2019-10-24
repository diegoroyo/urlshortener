package urlshortener.service;

import com.google.common.hash.Hashing;
import org.springframework.http.HttpStatus;
import urlshortener.domain.ShortURL;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.UUID;
import java.util.function.Function;

public class ShortURLBuilder {

    private var id: String? = null;
    private var target: String? = null;
    private var created: Date? = null;
    private var mode: Int? = null;
    private var active: Boolean = false;
    private var safe: Boolean? = null;
    private var ip: String? = null;
    private var country: String? = null;

    public fun build() = ShortURL(
        id,
        target,
        created,
        mode,
        active,
        safe,
        ip,
        country
    );

    public fun target(url: String) : ShortURLBuilder {
        this.target = url;
        this.id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
        return this;
    }

    public fun createdNow() : ShortURLBuilder {
        this.created = Date(System.currentTimeMillis());
        return this;
    }

    public fun temporaryRedirect() : ShortURLBuilder {
        this.mode = HttpStatus.TEMPORARY_REDIRECT.value();
        return this;
    }

    public fun makeActive() : ShortURLBuilder {
        this.active = true;
        return this;
    }

    public fun treatAsSafe() : ShortURLBuilder {
        this.safe = true;
        return this;
    }

    public fun ip(ip: String) : ShortURLBuilder {
        this.ip = ip;
        return this;
    }

    public fun unknownCountry() : ShortURLBuilder {
        this.country = null;
        return this;
    }
    
}
