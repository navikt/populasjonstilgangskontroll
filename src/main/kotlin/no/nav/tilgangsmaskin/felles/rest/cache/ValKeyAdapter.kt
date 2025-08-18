package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.lettuce.core.RedisClient
import io.lettuce.core.api.sync.RedisCommands
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.MeterBinder
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.Pingable
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.format
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ScanOptions.scanOptions
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ValKeyAdapter(private val cf: RedisConnectionFactory, cfg: ValKeyConfig,private vararg val cfgs: CachableRestConfig) : Pingable, MeterBinder {

    private val log = getLogger(javaClass)

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

    fun lookup(key: String) = lookup1<UUID>(key)


    private inline fun <reified T> lookup1(key: String): T? {
        val mapper = jacksonObjectMapper().apply {
            activateDefaultTyping(polymorphicTypeValidator, EVERYTHING, PROPERTY)
        }
        val commands: RedisCommands<String, String> = connection.sync()
        log.info("Lookup key: $key, type: ${T::class.java.simpleName}")
        val value = commands.get(key)
        log.info("Lookup key: $key, value: $value")
        return mapper.readValue<T>(value).also {
            log.info("Lookup key converted: $key, value: $it")

        }
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