package no.nav.tilgangsmaskin.felles.rest.cache

import io.lettuce.core.RedisURI.URI_SCHEME_REDIS_SECURE
import io.lettuce.core.RedisURI
import no.nav.tilgangsmaskin.felles.rest.cache.ValKeyAdapter.Companion.VALKEY
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(VALKEY)
data class ValKeyConfig(val username: String, val password: String, val host: String, val port: String, val uri: URI) {
    val redisURI = RedisURI.Builder
        .redis(host, port.toInt())
        .withSsl(true)
        .withAuthentication(username, password)
        .build()
    //val redisURI  = RedisURI.create("$URI_SCHEME_REDIS_SECURE://${username}:${password}@${host}:${port}");
}

