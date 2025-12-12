package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisClient
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter.nonLockingRedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.DatabindContext
import tools.jackson.databind.JavaType
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.jsontype.PolymorphicTypeValidator
import tools.jackson.databind.jsontype.PolymorphicTypeValidator.Validity.ALLOWED
import tools.jackson.databind.jsontype.PolymorphicTypeValidator.Validity.DENIED
import tools.jackson.module.kotlin.KotlinModule.Builder

@Configuration(proxyBeanMethods = true)
@ConditionalOnGCP
@EnableRedisRepositories
class CacheBeanConfig(private val cf: RedisConnectionFactory,
                      private vararg val cfgs: CachableRestConfig) : CachingConfigurer {


    private val mapper = JsonMapper.builder().polymorphicTypeValidator(NavPolymorphicTypeValidator()).apply {
        addModule(Builder().build())
        addModule(JacksonTypeInfoAddingValkeyModule())
    }.build()

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
    fun cacheClient(client: RedisClient,handler: CacheNøkkelHandler, sucessTeller: BulkCacheSuksessTeller, teller: BulkCacheTeller,manager: CacheManager) =
        CacheClient(client, handler, sucessTeller, teller,/* manager*/)

    @Bean
    fun cacheNøkkelHandler(mgr: RedisCacheManager) =
        CacheNøkkelHandler(mgr.cacheConfigurations,mapper)

    @Bean
    fun cacheHealthIndicator(adapter: CacheAdapter)  =
        PingableHealthIndicator(adapter)

    private fun cacheConfig(cfg: CachableRestConfig) =
        defaultCacheConfig()
            .entryTtl(cfg.varighet)
            .serializeKeysWith(fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(fromSerializer(GenericJacksonJsonRedisSerializer(mapper)))
            .apply {
                if (!cfg.cacheNulls) disableCachingNullValues()
            }
}

 class NavPolymorphicTypeValidator(private vararg val allowedPrefixes: String = arrayOf("no.nav.tilgangsmaskin","java.", "kotlin.")) : PolymorphicTypeValidator() {

    override fun validateBaseType(ctx: DatabindContext, base: JavaType) = validityFor(base.rawClass.name)

    override fun validateSubClassName(ctx: DatabindContext, base: JavaType, subClassName: String)  = validityFor(subClassName)

    override fun validateSubType(ctx: DatabindContext, base: JavaType, subType: JavaType) = validityFor(subType.rawClass.name)

    private fun validityFor(className: String) =
        if (allowedPrefixes.any { className.startsWith(it) }) ALLOWED else DENIED
}

