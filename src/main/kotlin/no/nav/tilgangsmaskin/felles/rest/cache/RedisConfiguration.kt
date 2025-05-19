package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.boot.conditionals.ConditionalOnDev
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
@ConditionalOnDev
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