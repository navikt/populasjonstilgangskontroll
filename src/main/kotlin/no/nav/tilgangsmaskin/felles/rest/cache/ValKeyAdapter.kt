package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.sync.RedisCommands
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.MeterBinder
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.Pingable
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.format
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ScanOptions.scanOptions
import org.springframework.stereotype.Component

@Component
class ValKeyAdapter(private val cf: RedisConnectionFactory, cfg: ValKeyConfig,private vararg val cfgs: CachableRestConfig, val mapper: ObjectMapper) : Pingable, MeterBinder {

    override val pingEndpoint  =  "${cfg.host}:${cfg.port}"
    override val name = "ValKey Cache"
     val connection = RedisClient.create(cfg.valkeyURI).connect()


    override fun ping() =
        cf.connection.use {
            if (it.ping().equals("PONG", ignoreCase = true)) {
                emptyMap<String,String>()
            }
            else {
                error("$name ping failed")
            }
        }

    fun lookup(key: String) = lookup(key,UUID::class)


    private inline fun <reified T> lookup(key: String, clazz: T): T? {
        val commands: RedisCommands<String, String> = connection.sync()
        val value = commands.get(key)
        return mapper.readValue<T>(value)
    }

    fun cacheSizes() = cfgs.associate { it.navn to "${cacheSize(it.navn).toLong()} innslag, ttl: ${it.varighet.format()}" }

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

    companion object {
        const val VALKEY = "valkey"
    }
}