package no.nav.tilgangsmaskin.felles.rest.cache

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.MeterBinder
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.Pingable
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ScanOptions.scanOptions
import org.springframework.stereotype.Component
import java.util.Properties

@Component
class ValKeyAdapter(private val cf: RedisConnectionFactory, cfg: ValKeyConfig,private vararg val cfgs: CachableRestConfig) : Pingable, MeterBinder {


    private val log = getLogger(javaClass)

    override val pingEndpoint  =  "${cfg.host}:${cfg.port}"
    override val name = "ValKey Cache"

    override fun ping() =
        cf.connection.use {
            if (it.ping().equals("PONG", ignoreCase = true)) {
                emptyMap<String,String>()
            }
            else {
                error("$name ping failed")
            }
        }


    private fun cacheSizes()  = cfgs.associate { it.navn to "${cacheSize(it.navn).toLong()} innslag, ttl: ${it.varighet.toHours()} timer" }

    override fun bindTo(registry: MeterRegistry) {
        cfgs.forEach { cfg ->
            registry.gauge("cache.size", Tags.of("navn", cfg.navn), cf) {
                cacheSize( cfg.navn)
            }
        }
    }

    private fun cacheSize(cacheName: String) =
        cf.connection.use {
            it.keyCommands()
                .scan(scanOptions()
                    .match("*$cacheName*")
                    .count(1000)
                    .build())
                .asSequence()
                .count()
                .toDouble()
        }
}