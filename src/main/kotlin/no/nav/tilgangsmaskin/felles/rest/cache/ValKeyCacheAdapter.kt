package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.lettuce.core.KeyValue
import io.lettuce.core.RedisClient
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.MeterBinder
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.Pingable
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.format
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ScanOptions.scanOptions
import org.springframework.stereotype.Component

@Component
class ValKeyCacheAdapter(cacheManager: RedisCacheManager, private val cf: RedisConnectionFactory, cfg: ValKeyConfig, private vararg val cfgs: CachableRestConfig) : Pingable, MeterBinder {

    private val log = getLogger(javaClass)

    private val prefixes = cacheManager.cacheConfigurations

    override val pingEndpoint  =  "${cfg.host}:${cfg.port}"
    override val name = "ValKey Cache"
     val conn = RedisClient.create(cfg.valkeyURI).connect()
    private val mapper = jacksonObjectMapper().apply {
        activateDefaultTyping(polymorphicTypeValidator, EVERYTHING, PROPERTY)
    }

    override fun ping() =
        cf.connection.use {
            if (it.ping().equals("PONG", ignoreCase = true)) {
                emptyMap<String,String>()
            }
            else {
                error("$name ping failed")
            }
        }

    fun skjerminger(navIds: Set<String>) = get<Boolean>(SKJERMING,navIds)
    fun personer(navIds: Set<String>) = get<Person>(PDL,navIds)


    fun cacheSizes() = cfgs.associate { it.navn to "${cacheSize(it.navn).toLong()} innslag, ttl: ${it.varighet.format()}" }

    override fun bindTo(registry: MeterRegistry) {
        cfgs.forEach { cfg ->
            registry.gauge("cache.size", Tags.of("navn", cfg.navn), cf) {
                cacheSize((prefixes[cfg.navn]!!.getKeyPrefixFor(cfg.navn)))
            }
        }
    }

    private fun cacheSize(prefix: String) =
        cf.connection.use {
            it.keyCommands()
                .scan(scanOptions()
                    .match("$prefix*")
                    .count(1000)
                    .build())
                .asSequence()
                .count()
                .toDouble()
        }

    private inline fun <reified T> get(cache: String, id: String) =
        conn.sync().get(id.prefixed(cache))?.let {key ->
            mapper.readValue<T>(key)
        }

    private inline fun <reified T> get(cache: String, ids: Set<String>)  =
        if (ids.isEmpty()) {
            emptySet()
        }
        else conn.sync()
            .mget(*ids.map {key ->
                key.prefixed(cache)
            }.toTypedArray<String>()).filter {
                it.hasValue()
            }
            .map<KeyValue<String, String>, Pair<String, T>> {
                it.key.unprefixed(cache) to mapper.readValue<T>(it.value)
            }.toSet()

    fun put(cache: String, innslag: Map<String, Any>): Int {
        if (innslag.isEmpty()) return 0
        conn.sync().mset(
            innslag.mapKeys { it.key.prefixed(cache) }
                .mapValues { mapper.writeValueAsString(it.value) }
        )
        return innslag.size
    }


    private fun String.prefixed(cache: String) = "${prefixes.prefixFor(cache)}$this"

    private fun String.unprefixed(cache: String) = removePrefix(prefixes.prefixFor(cache))

    private fun Map<String, RedisCacheConfiguration>.prefixFor(cache: String) = get(cache)?.getKeyPrefixFor(cache) ?: throw IllegalStateException("Har ingen cache med navn $cache")


    companion object {
        const val VALKEY = "valkey"
    }
}