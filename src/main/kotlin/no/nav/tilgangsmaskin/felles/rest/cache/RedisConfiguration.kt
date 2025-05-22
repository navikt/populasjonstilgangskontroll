package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.core.JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
class RedisConfiguration(private val cf: RedisConnectionFactory) : CachingConfigurer {


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
            .entryTtl(Duration.ofHours(24)) // Example: 10 min TTL
        val cacheConfigs = mapOf(PDL to customCacheConfig, SKJERMING to customCacheConfig, GRAPH to customCacheConfig)

        return RedisCacheManager.builder(nonLockingRedisCacheWriter(cf))
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

    private val mapper =
        jacksonObjectMapper().registerModule(JavaTimeModule())
       // m.copy()
            .apply {
                configure(INCLUDE_SOURCE_IN_LOCATION, true)
                activateDefaultTyping(polymorphicTypeValidator,
            EVERYTHING,
            PROPERTY
        )
    }
}