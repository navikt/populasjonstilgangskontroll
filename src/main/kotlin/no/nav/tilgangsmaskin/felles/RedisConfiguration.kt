package no.nav.tilgangsmaskin.felles

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import no.nav.boot.conditionals.ConditionalOnDev
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisConnectionUtils
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

//@Configuration
//@EnableCaching
//@ConditionalOnDev
class RedisConfiguration(private val mapper: ObjectMapper) : CachingConfigurer {
    /*
        @Bean
        fun redisHealthIndicator(cf: RedisConnectionFactory) = object : HealthIndicator {
            override fun health() =
                RedisConnectionUtils.getConnection(cf).use { connection ->
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

       // @Bean
        fun redisConfig(): RedisCacheConfiguration {
            val copy = mapper.copy().apply {
                val typeValidator = BasicPolymorphicTypeValidator.builder()
                    .build()
                activateDefaultTyping(
                    typeValidator,
                    ObjectMapper.DefaultTyping.EVERYTHING,
                    JsonTypeInfo.As.PROPERTY
                )
            }
            return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        GenericJackson2JsonRedisSerializer(copy)
                    )
                )
        }*/

    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class)
            append(method.name)
            params.forEach { append(it) }
        }
    }
}

