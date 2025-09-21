package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
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
class ValKeyBeanConfigurer(private val cf: RedisConnectionFactory,
                           mapper: ObjectMapper,
                           private vararg val cfgs: CachableRestConfig) : CachingConfigurer {

    private val valKeyMapper =
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
    fun valkeyClient(cfg: ValKeyConfig): RedisClient =
        RedisClient.create(cfg.valkeyURI)

    @Bean
    @Qualifier("cacheConnection")
    fun cacheConnection(client: RedisClient) = client.connect().apply {
      //  sync().configSet("notify-keyspace-events", "Ex")
    }

    @Bean
    fun valkeyCacheClient(@Qualifier("cacheConnection") cacheConnection: StatefulRedisConnection<String,String>, handler: ValkeyCacheKeyHandler, sucessTeller: BulkCacheSuksessTeller, teller: BulkCacheTeller) =
        ValkeyCacheClient(handler,
            cacheConnection,
            valKeyMapper,sucessTeller,teller)

    @Bean
    fun cachePrefixes(cfgs: Map<String, RedisCacheConfiguration>) = cfgs.mapValues { it.value.keyPrefix}

    @Bean
    fun valKeyHealthIndicator(adapter: ValKeyCacheAdapter)  =
        PingableHealthIndicator(adapter)

    @Bean
    fun cacheConfigurations(mgr: RedisCacheManager)  = mgr.cacheConfigurations

    private fun cacheConfig(cfg: CachableRestConfig) =
        defaultCacheConfig()
            .entryTtl(cfg.varighet)
            .serializeKeysWith(fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(fromSerializer(GenericJackson2JsonRedisSerializer(valKeyMapper)))
            .apply {
                if (!cfg.cacheNulls) disableCachingNullValues()
            }
}