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
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
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
import java.time.Duration
import kotlin.reflect.jvm.jvmName

@Configuration
@EnableCaching
@ConditionalOnGCP
class ValkeyConfiguration(private val cf: RedisConnectionFactory, private vararg val cfgs: CachableRestConfig) : CachingConfigurer {

    private val log = getLogger(ValkeyConfiguration::class.java)


    @Bean
    fun valkeyCacheSizeMeterBinder(template: StringRedisTemplate) = MeterBinder { registry ->
            cfgs.forEach { cfg ->
                registry.gauge("cache.size", Tags.of("navn", cfg.navn), template) { template ->
                    cacheSize( cfg.navn)
                }
            }
        }

    @Bean
    fun valkeyHealthIndicator( @Value("\${valkey.host.cache}") host: String,@Value("\${valkey.port.cache}") port: String  ) = HealthIndicator {
        cf.connection.use { connection ->
            runCatching {
                if (connection.ping().equals("PONG", ignoreCase = true)) {
                    Health.up()
                        .withDetail("ValKey","I toppform")
                        .withDetail("endpoint", "$host:$port")
                        . withDetails(cacheSizes())
                        .build()
                } else {
                    Health.down()
                        .withDetail("ValKey", "Ikke helt i slag i dag")
                        .build()
                }
            }.getOrElse {
                log.warn("Feil ved ping av ValKey", it)
                Health.down(it)
                    .withDetail("ValKey", "Ingen forbindelse")
                    .withException(it)
                    .build() }
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

    private fun cacheSizes() = cfgs.associate { it.navn to "${cacheSize(it.navn)} innslag i cache"}

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