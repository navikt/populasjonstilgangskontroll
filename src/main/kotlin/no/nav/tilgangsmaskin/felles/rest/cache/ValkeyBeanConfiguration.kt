package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.core.JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.MeterBinder
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.Pingable
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter.lockingRedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ScanOptions.scanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.reflect.jvm.jvmName

@Configuration
@EnableCaching
@ConditionalOnGCP
class ValkeyBeanConfiguration(private val cf: RedisConnectionFactory, private vararg val cfgs: CachableRestConfig, @Value("\${valkey.host.cache}") private val host: String, @Value("\${valkey.port.cache}") private val port: String ) : CachingConfigurer, Pingable {
    private val log = getLogger(ValkeyBeanConfiguration::class.java)

    override val pingEndpoint  = "$host:$port"
    override val name = "ValKey Cache"
    override val isEnabled = true

    @Bean
    fun valkeyCacheSizeMeterBinder(template: StringRedisTemplate) = MeterBinder { registry ->
            cfgs.forEach { cfg ->
                registry.gauge("cache.size", Tags.of("navn", cfg.navn), template) { template ->
                    cacheSize( cfg.navn)
                }
            }
        }

    @Bean
    override fun cacheManager(): RedisCacheManager =
        RedisCacheManager.builder(lockingRedisCacheWriter(cf))
            .withInitialCacheConfigurations(cfgs.associate { it.navn to cacheConfig(it) })
            .enableStatistics()
            .build()

    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class.jvmName)
            append(":")
            append(method.name)
            append(":")
            params.forEach {
               if (it is BrukerId)
               append(it.verdi)
                 else append(it)

            }
        }
    }
    override fun ping() =
        cf.connection.use {
            if (it.ping().equals("PONG", ignoreCase = true)) {
                emptyMap<String,String>()
            }
            else {
                throw IllegalStateException("$name ping failed")
            }
        }


    fun cacheSizes() = cfgs.associate { it.navn to "${cacheSize(it.navn).toLong()} innslag i cache"}

    private fun cacheSize(cacheName: String) =
        runCatching {
            val scanOptions = scanOptions().match("*$cacheName*").count(1000).build()
            cf.connection
                .keyCommands()
                .scan(scanOptions)
                .asSequence()
                .count()
                .toDouble()
        }.getOrElse {
            log.warn("Kunne ikke hente størrelse på cache $cacheName", it)
            0.0
        }

    private fun cacheConfig(cfg: CachableRestConfig) =
        defaultCacheConfig()
            .disableCachingNullValues()
            .entryTtl(Duration.ofHours(cfg.expireHours))
            .serializeKeysWith(fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(fromSerializer(GenericJackson2JsonRedisSerializer(mapper)))

    private val mapper =
        jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .apply {
                configure(INCLUDE_SOURCE_IN_LOCATION, true)
                activateDefaultTyping(polymorphicTypeValidator,
                    EVERYTHING,
                    PROPERTY
                )
            }
}

@Component
class ValkeyHealthIndicator(config: ValkeyBeanConfiguration) : PingableHealthIndicator(config)
