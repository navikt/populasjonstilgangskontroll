package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.boot.conditionals.EnvUtil.isDevOrLocal
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
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
    override fun cacheManager(): RedisCacheManager =
        RedisCacheManager.builder(nonLockingRedisCacheWriter(cf))
            .withInitialCacheConfigurations(cfgs.associate { it.navn to cacheConfig(it) })
            .enableStatistics()
            .build()

    @Bean
    fun prefixes(mgr: RedisCacheManager) =
        mgr.cacheConfigurations.map { (key,value) -> key to value.keyPrefix }

    private fun cacheConfig(cfg: CachableRestConfig) =
         defaultCacheConfig()
            .entryTtl(cfg.varighet)
            .serializeKeysWith(fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(fromSerializer(GenericJackson2JsonRedisSerializer(valKeyMapper)))
            .apply {
                if (!cfg.cacheNulls) disableCachingNullValues()
            }

    @Bean
    fun valKeyHealthIndicator(adapter: ValKeyAdapter)  = PingableHealthIndicator(adapter)
}

