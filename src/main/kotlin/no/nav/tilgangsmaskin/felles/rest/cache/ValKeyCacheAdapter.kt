package no.nav.tilgangsmaskin.felles.rest.cache

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.MeterBinder
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.Pingable
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.format
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ScanOptions.scanOptions
import org.springframework.stereotype.Component

@Component
class ValKeyCacheAdapter(private val handler: ValkeyCacheKeyMapper, private val cf: RedisConnectionFactory, cfg: ValKeyConfig, private vararg val cfgs: CachableRestConfig) : Pingable, MeterBinder {

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

    fun cacheSizes() = cfgs.associate { it.navn to "${cacheSize(it.navn).toLong()} innslag, ttl: ${it.varighet.format()}" }

    override fun bindTo(registry: MeterRegistry) {
        cfgs.forEach { cfg ->
            registry.gauge("cache.size", Tags.of("navn", cfg.navn), cf) {
                cacheSize((handler.configs[cfg.navn]!!.getKeyPrefixFor(cfg.navn)))
            }
        }
    }

    private fun cacheSize(prefix: String) =
        cf.connection.use {
            it.keyCommands()
                .scan(scanOptions()
                    .match("$prefix*")
                    .count(10000)
                    .build())
                .asSequence()
                .count()
                .toDouble()
        }


    companion object {
        const val VALKEY = "valkey"
    }
}

