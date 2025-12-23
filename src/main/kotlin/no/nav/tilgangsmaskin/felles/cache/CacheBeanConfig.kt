package no.nav.tilgangsmaskin.felles.cache

import glide.api.GlideClient
import glide.api.models.GlideString.gs
import glide.api.models.configuration.BaseSubscriptionConfiguration.MessageCallback
import glide.api.models.configuration.GlideClientConfiguration
import glide.api.models.configuration.NodeAddress
import glide.api.models.configuration.ServerCredentials
import glide.api.models.configuration.StandaloneSubscriptionConfiguration
import glide.api.models.configuration.StandaloneSubscriptionConfiguration.PubSubChannelMode.EXACT
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
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter.nonLockingRedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule.Builder


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
    fun lettuceClient(cfg: CacheConfig) =
        RedisClient.create(RedisURI.Builder
            .redis(cfg.host, cfg.port)
            .withSsl(true)
            .withAuthentication(cfg.username, cfg.password)
            .build())

    @Bean
    fun glideConfig(cfg: CacheConfig, callback: GlideCacheElementUtløptLytter) =
        GlideClientConfiguration.builder()
            .address(NodeAddress.builder()
                .host(cfg.host)
                .port(cfg.port)
                    .build())
            .useTLS(true)
            .credentials(ServerCredentials.builder()
                .username(cfg.username)
                .password(cfg.password)
                .build())
            .subscriptionConfiguration(StandaloneSubscriptionConfiguration.builder()
                .subscription(EXACT, gs("__keyevent@0__:expired"))
                .callback(callback)
                .build())
            .build()

    @Bean
    fun glideClient(cfg: GlideClientConfiguration)  =
        runCatching {
            GlideClient.createClient(cfg)
        }.getOrElse {
            throw RuntimeException("Feil ved opprettelse av GlideClient mot ${cfg.addresses}", it)
        }
        GlideClient.createClient(cfg).get()

    @Bean
    fun cacheNøkkelHandler(mgr: RedisCacheManager) =
        CacheNøkkelHandler(mgr.cacheConfigurations,MAPPER)

    @Bean
    fun cacheHealthIndicator(client: CacheOperations)  =
        PingableHealthIndicator(client)

    private fun cacheConfig(cfg: CachableRestConfig) =
        defaultCacheConfig()
            .entryTtl(cfg.varighet)
            .serializeKeysWith(fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(fromSerializer(GenericJacksonJsonRedisSerializer(MAPPER)))
            .apply {
                if (!cfg.cacheNulls) disableCachingNullValues()
            }

    companion object {
        val MAPPER = JsonMapper.builder()
            .polymorphicTypeValidator(NavPolymorphicTypeValidator()).apply {
                addModule(Builder().build())
                addModule(JacksonTypeInfoAddingValkeyModule())
            }.build()
    }
}


class AllCaches(val map: Map<String,List<CachableConfig>>)