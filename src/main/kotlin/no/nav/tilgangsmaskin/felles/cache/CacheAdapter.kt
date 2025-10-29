package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.MeterBinder
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.Pingable
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.stereotype.Component

@Component
class CacheAdapter( private val client: CacheClient,private val cf: RedisConnectionFactory, cfg: CacheConfig, private vararg val cfgs: CachableRestConfig) : Pingable, MeterBinder {

    private val log = getLogger(javaClass)

    override val pingEndpoint  =  "${cfg.host}:${cfg.port}"
    override val name = "Cache"

    override fun ping() =
        cf.connection.use {
            if (it.ping().equals("PONG", ignoreCase = true)) {
                emptyMap<String,String>()
            }
            else {
                error("$name ping failed")
            }
        }

    override fun bindTo(registry: MeterRegistry) {
        client.cacheStørrelser().forEach { (navn, størrelse) ->
           registry.gauge("cache.size", Tags.of("navn", navn), cf) {
               størrelse.toDouble()
           }
        }
    }

    companion object {
        const val VALKEY = "valkey"
    }
}

