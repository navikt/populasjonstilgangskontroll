package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.Pingable
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.stereotype.Component

@Component
class CacheAdapter(private val cf: RedisConnectionFactory, cfg: CacheConfig, private vararg val cfgs: CachableRestConfig) : Pingable {

    override val pingEndpoint  =  "${cfg.host}:${cfg.port}"
    override val name = "Cache"

    override fun ping() =
        cf.connection.use {
            if (it.ping().equals(PONG, ignoreCase = true)) {
                emptyMap<String,String>()
            }
            else {
                error("$name ping failed")
            }
        }
    companion object {
        private const val PONG = "pong"
        const val VALKEY = "valkey"
    }
}

