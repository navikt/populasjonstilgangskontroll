package no.nav.tilgangsmaskin.felles.cache

import glide.api.GlideClient
import glide.api.models.configuration.GlideClientConfiguration
import glide.api.models.configuration.NodeAddress
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.boot.conditionals.ConditionalOnProd
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter.nonLockingRedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule.Builder
import java.util.concurrent.CompletableFuture


@Configuration(proxyBeanMethods = true)
@ConditionalOnGCP
class CacheBeanConfig(private val cf: RedisConnectionFactory,
                      private vararg val cfgs: CachableRestConfig) : CachingConfigurer {


    @Bean
    override fun cacheManager()  =
        RedisCacheManager.builder(nonLockingRedisCacheWriter(cf))
            .withInitialCacheConfigurations(cfgs.associate { it.navn to cacheConfig(it) })
            .enableStatistics()
            .build()

    @Bean
    //@ConditionalOnProd
    fun lettuceClient(cfg: CacheConfig) =
        RedisClient.create(RedisURI.Builder
            .redis(cfg.host, cfg.port)
            .withSsl(true)
            .withAuthentication(cfg.username, cfg.password)
            .build())

    @Bean
    @ConditionalOnNotProd
    @Lazy
    fun glideConfig(cfg: CacheConfig) =
        GlideClientConfiguration.builder()
            .address(NodeAddress.builder().host(cfg.host).port(cfg.port).build())
            .build()
    @Bean
    @ConditionalOnNotProd
    @Lazy
    fun glideClient(cfg: GlideClientConfiguration)  =
        GlideClient.createClient(cfg)

    @Bean
    @ConditionalOnNotProd
    @Lazy
    fun glideCacheClient(client: CompletableFuture<GlideClient>, handler: CacheNøkkelHandler) =
        GlideCacheClient(client, handler)

        @Bean
    fun cacheNøkkelHandler(mgr: RedisCacheManager) =
        CacheNøkkelHandler(mgr.cacheConfigurations,MAPPER)

    @Bean
    fun cacheHealthIndicator(cache: CacheOperations)  =
        PingableHealthIndicator(cache)

    private fun cacheConfig(cfg: CachableRestConfig) =
        defaultCacheConfig()
            .entryTtl(cfg.varighet)
            .serializeKeysWith(fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(fromSerializer(GenericJacksonJsonRedisSerializer(MAPPER)))
            .apply {
                if (!cfg.cacheNulls) disableCachingNullValues()
            }

    companion object {
         val MAPPER: JsonMapper = JsonMapper.builder().polymorphicTypeValidator(NavPolymorphicTypeValidator()).apply {
            addModule(Builder().build())
            addModule(JacksonTypeInfoAddingValkeyModule())
        }.build()
    }
}


class AllCaches(val map: Map<String,List<CachableConfig>>)