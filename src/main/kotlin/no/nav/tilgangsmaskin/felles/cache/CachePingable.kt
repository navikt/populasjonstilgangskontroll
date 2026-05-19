package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.Pingable
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.stereotype.Component
import java.net.URI

@Component
class CachePingable(private val cf: RedisConnectionFactory, cfg: CacheConfig) : Pingable {

    override val pingEndpoint = URI.create("${cfg.host}:${cfg.port}")
    override val name = "Cache"

    override fun ping() =
        cf.connection.use {
            if (it.ping().equals(PONG, ignoreCase = true)) {
                emptyMap<String, String>()
            } else {
                error("$name ping failed")
            }
        }

    companion object {
        private const val PONG = "pong"
        const val VALKEY = "valkey"
    }
}

