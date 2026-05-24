package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.ClientOptions
import io.lettuce.core.RedisClient
import io.lettuce.core.SocketOptions
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.cache.interceptor.LoggingCacheErrorHandler
import org.springframework.data.redis.cache.RedisCacheWriter.nonLockingRedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule.Builder

@Configuration(proxyBeanMethods = true)
@ConditionalOnGCP
@NoCoverageAnalysis
class CacheBeanConfig(private val cf: RedisConnectionFactory,
                      private vararg val cfgs: CachableRestConfig) : CachingConfigurer {


    override fun errorHandler() =
        LoggingCacheErrorHandler(true)


    @Bean
    override fun cacheManager() =
        RedisCacheManager.builder(nonLockingRedisCacheWriter(cf))
            .withInitialCacheConfigurations(cfgs.associate { it.navn to cacheConfig(it) })
            .enableStatistics()
            .build()

    @Bean
    fun redisClient(cfg: CacheConfig) =
        RedisClient.create(cfg.cacheURI).apply {
            options = ClientOptions.builder()
                .socketOptions(
                    SocketOptions.builder()
                        .connectTimeout(cfg.connectTimeout)
                        .build()
                )
                .build()
        }

    @Bean
    fun cacheNøkkelHandler(mgr: RedisCacheManager) =
        CacheNøkkelMapper(mgr.cacheConfigurations)

    @Bean
    fun cacheHealthIndicator(pingable: CachePingable) =
        PingableHealthIndicator(pingable)

    private fun cacheConfig(cfg: CachableRestConfig) =
        defaultCacheConfig()
            .entryTtl(cfg.varighet)
            .serializeKeysWith(fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(fromSerializer(GenericJacksonJsonRedisSerializer(VALKEY_MAPPER)))
            .apply {
                if (!cfg.cacheNulls) disableCachingNullValues()
            }

    companion object {
        val VALKEY_MAPPER = JsonMapper.builder().polymorphicTypeValidator(NavPolymorphicTypeValidator()).apply {
            addModule(Builder().build())
            addModule(JacksonTypeInfoAddingValkeyModule())
        }.build()
    }
}

