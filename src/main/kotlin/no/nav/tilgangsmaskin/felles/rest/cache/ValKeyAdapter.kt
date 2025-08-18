package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.lettuce.core.RedisClient
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.MeterBinder
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste.Companion.ENTRA_OID
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.Pingable
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.format
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ScanOptions.scanOptions
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ValKeyAdapter(cacheManager: RedisCacheManager, private val cf: RedisConnectionFactory, cfg: ValKeyConfig, private vararg val cfgs: CachableRestConfig) : Pingable, MeterBinder {

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

    fun lookup(cache: String, key: String) = doLookup<UUID>(cache, key)

    private inline fun <reified T> doLookup(cache: String, key: String) = mapper.readValue<T>(conn.sync().get("${prefixes[ENTRA_OID]?.keyPrefix}::$key"))

    fun cacheSizes() = cfgs.associate { it.navn to "${cacheSize(it.navn).toLong()} innslag, ttl: ${it.varighet.format()}" }

    override fun bindTo(registry: MeterRegistry) {
        cfgs.forEach { cfg ->
            registry.gauge("cache.size", Tags.of("navn", cfg.navn), cf) {
                val prefix = (prefixes[cfg.navn]!!.getKeyPrefixFor(cfg.navn))
                log.infp("Cache size for prefix '$prefix' is being measured")
                cacheSize(prefix)
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

    companion object {
        const val VALKEY = "valkey"
    }
}