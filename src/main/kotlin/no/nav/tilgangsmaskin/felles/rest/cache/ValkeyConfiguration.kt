package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.core.JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
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
import org.springframework.data.redis.core.RedisConnectionUtils.getConnection
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration
import kotlin.reflect.jvm.jvmName

@Configuration
@EnableCaching
@ConditionalOnGCP
class ValkeyConfiguration(private val cf: RedisConnectionFactory, private vararg val cfgs: CachableRestConfig) : CachingConfigurer {


    @Bean
        fun redisHealthIndicator() = HealthIndicator {
            getConnection(cf).use { connection ->
                runCatching {
                    if (connection.ping().equals("PONG", ignoreCase = true)) {
                        Health.up().withDetail("Redis", "Frisk og rask").build()
                    } else {
                        Health.down().withDetail("Redis", "Ikke helt i slag i dag").build()
                    }
                }.getOrElse { Health.down(it).withDetail("Redis", "Ingen forbindelse").build() }
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
            append(method.name)
            params.forEach { append(it) }
        }
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