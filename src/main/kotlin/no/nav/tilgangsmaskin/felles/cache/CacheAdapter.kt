package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.MeterBinder
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.Pingable
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.format
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.text.get

@Component
class CacheAdapter( private val handler: CacheNøkkelHandler,private val client: CacheClient,private val cf: RedisConnectionFactory, cfg: CacheConfig, private vararg val cfgs: CachableRestConfig) : Pingable, MeterBinder {

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


   fun cacheSizes() = cfgs.associate { it.navn to "${client.cacheSize(it.navn).toLong()} innslag, ttl: ${it.varighet.format()}" }

    override fun bindTo(registry: MeterRegistry) {
        cfgs.forEach { cfg ->
            registry.gauge("cache.size", Tags.of("navn", cfg.navn), cf) {
                runBlocking {
                    try {
                        withTimeout(Duration.ofSeconds(1).toMillis()) {
                            client.cacheSize(handler.configs[cfg.navn]!!.getKeyPrefixFor(cfg.navn))
                        }
                    } catch (e: TimeoutCancellationException) {
                        log.warn("Timeout ved henting av cache size for ${cfg.navn}")
                        0.toDouble()
                    }
                }
            }
        }
    }


    companion object {
        const val VALKEY = "valkey"
    }
}

