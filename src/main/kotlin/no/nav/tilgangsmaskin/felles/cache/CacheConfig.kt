package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.cache.CacheConfig.Companion.VALKEY
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(VALKEY)
data class CacheConfig(val username: String, val password: String, val host: String, val port: Int) {

    companion object {
        const val VALKEY = "valkey"
    }
}

