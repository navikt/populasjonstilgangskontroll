package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisURI
import no.nav.tilgangsmaskin.felles.cache.CacheAdapter.Companion.VALKEY
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(VALKEY)
data class CacheConfig(val username: String, val password: String, val host: String, val port: Int) {
    val cacheURI = RedisURI.Builder
        .redis(host, port)
        .withSsl(true)
        .withAuthentication(username, password)
        .build()
}

