package no.nav.tilgangsmaskin.felles.rest.cache

import io.lettuce.core.RedisURI
import no.nav.tilgangsmaskin.felles.rest.cache.ValKeyAdapter.Companion.VALKEY
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(VALKEY)
data class ValKeyConfig(val username: String, val password: String, val host: String, val port: String, val uri: URI) {
    val redisURI  = RedisURI.create("rediss://${username}:${password}@${uri}");
}

