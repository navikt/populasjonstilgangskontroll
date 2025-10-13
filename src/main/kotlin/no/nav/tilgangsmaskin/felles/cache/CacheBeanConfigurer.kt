package no.nav.tilgangsmaskin.felles.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import io.lettuce.core.RedisClient
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter.nonLockingRedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration(proxyBeanMethods = true)
@EnableCaching
@ConditionalOnGCP
class CacheBeanConfigurer(private val cf: RedisConnectionFactory, mapper: ObjectMapper,
                          private vararg val cfgs: CachableRestConfig) : CachingConfigurer {

    private val cacheMapper =
        mapper.copy().apply {
            activateDefaultTyping(polymorphicTypeValidator, EVERYTHING, PROPERTY)
        }

    @Bean
    override fun cacheManager()  =
        RedisCacheManager.builder(nonLockingRedisCacheWriter(cf))
            .withInitialCacheConfigurations(cfgs.associate { it.navn to cacheConfig(it) })
            .enableStatistics()
            .build()

    @Bean
    fun redisClient(cfg: CacheConfig) =
        RedisClient.create(cfg.cacheURI)

    @Bean
    fun cacheClient(redisClient: RedisClient,handler: CacheNøkkelHandler, sucessTeller: BulkCacheSuksessTeller, teller: BulkCacheTeller) =
        CacheClient(redisClient, handler, sucessTeller, teller)

    @Bean
    fun cacheNøkkelHandler(mgr: RedisCacheManager) =
        CacheNøkkelHandler(mgr.cacheConfigurations,cacheMapper)

    @Bean
    fun cacheHealthIndicator(adapter: CacheAdapter)  =
        PingableHealthIndicator(adapter)

    private fun cacheConfig(cfg: CachableRestConfig) =
        defaultCacheConfig()
            .entryTtl(cfg.varighet)
            .serializeKeysWith(fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(fromSerializer(GenericJackson2JsonRedisSerializer(cacheMapper)))
            .apply {
                if (!cfg.cacheNulls) disableCachingNullValues()
            }
}