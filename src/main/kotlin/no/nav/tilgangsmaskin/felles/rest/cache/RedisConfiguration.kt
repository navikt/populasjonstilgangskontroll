package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.boot.conditionals.ConditionalOnDev
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter.nonLockingRedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisConnectionUtils.getConnection
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
@ConditionalOnDev
class RedisConfiguration(private val cf: RedisConnectionFactory, private val mapper: ObjectMapper) : CachingConfigurer {


        @Bean
        fun redisHealthIndicator() = object : HealthIndicator {
            override fun health() =
                getConnection(cf).use { connection ->
                    runCatching {
                        if (connection.ping().equals("PONG", ignoreCase = true)) {
                            Health.up().withDetail("Redis", "Connection is healthy").build()
                        } else {
                            Health.down().withDetail("Redis", "Ping failed").build()
                        }
                    }.fold(
                        onSuccess = { it },
                        onFailure = { Health.down(it).withDetail("Redis", "Connection failed").build() }
                    )
                }
        }

    @Bean
    override fun cacheManager(): RedisCacheManager {
        val keySerializer = StringRedisSerializer()
        val valueSerializer = GenericJackson2JsonRedisSerializer(mapper)
        val customCacheConfig = defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
            .entryTtl(Duration.ofMinutes(10)) // Example: 10 min TTL
        val cacheConfigs = mapOf(PDL to customCacheConfig, SKJERMING to customCacheConfig, GRAPH to customCacheConfig)

        return RedisCacheManager.builder(nonLockingRedisCacheWriter(cf))
            .cacheDefaults(defaultCacheConfig())
            .withInitialCacheConfigurations(cacheConfigs)
            .enableStatistics()
            .build()
    }


    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class)
            append(method.name)
            params.forEach { append(it) }
        }
    }
}