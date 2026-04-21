package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisURI
import no.nav.tilgangsmaskin.felles.cache.CacheAdapter.Companion.VALKEY
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(VALKEY)
data class CacheConfig(val username: String, val password: String, val host: String, val port: Int, val timeout: Duration, val connectTimeout: Duration) {
    val cacheURI = RedisURI.Builder
        .redis(host, port)
        .withSsl(true)
        .withAuthentication(username, password)
        .build()
}

