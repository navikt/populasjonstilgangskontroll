package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.cache.CacheConfig.Companion.VALKEY
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import kotlin.reflect.KClass

@ConfigurationProperties(VALKEY)
data class CacheConfig(val username: String, val password: String, val host: String, val port: Int, val timeout: Duration = Duration.ofSeconds(5), val clazz: KClass<*>) {

    companion object {
        const val VALKEY = "valkey"
    }
}

