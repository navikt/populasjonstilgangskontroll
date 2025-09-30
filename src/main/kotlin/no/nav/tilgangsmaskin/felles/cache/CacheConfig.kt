package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisURI
import no.nav.tilgangsmaskin.felles.cache.CacheAdapter.Companion.VALKEY
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(VALKEY)
data class CacheConfig(val username: String, val password: String, val host: String, val port: String, val uri: URI) {
    val valkeyURI = RedisURI.Builder
        .redis(host, port.toInt())
        .withSsl(true)
        .withAuthentication(username, password)
        .build()
}

