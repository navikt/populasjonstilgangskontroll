package no.nav.tilgangsmaskin.felles.cache

import glide.api.GlideClient
import glide.api.models.GlideString.gs
import glide.api.models.configuration.BackoffStrategy
import glide.api.models.configuration.GlideClientConfiguration
import glide.api.models.configuration.NodeAddress
import glide.api.models.configuration.ServerCredentials
import glide.api.models.configuration.StandaloneSubscriptionConfiguration
import glide.api.models.configuration.StandaloneSubscriptionConfiguration.PubSubChannelMode.EXACT
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOperations.Companion.`UTLØPT_KANAL`
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.slf4j.LoggerFactory.getLogger
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
import tools.jackson.module.kotlin.KotlinModule
import java.util.concurrent.TimeUnit.SECONDS


@Configuration(proxyBeanMethods = true)
@ConditionalOnGCP
class CacheBeanConfig(private val cf: RedisConnectionFactory,
                      private vararg val cfgs: CachableRestConfig) : CachingConfigurer {


    private val log = getLogger(javaClass)

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
            .withTimeout(cfg.timeout)
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
            .reconnectStrategy(BackoffStrategy.builder()
                .numOfRetries(2)
                .build())
            .requestTimeout(cfg.timeout.toSeconds().toInt())
            .credentials(ServerCredentials.builder()
                .username(cfg.username)
                .password(cfg.password)
                .build())
            .subscriptionConfiguration(StandaloneSubscriptionConfiguration.builder()
                .subscription(EXACT, gs(UTLØPT_KANAL))
                .callback(callback)
                .build())
            .build()

    @Bean
    @Lazy
     fun glideClient(cfg: GlideClientConfiguration)  =
            GlideClient.createClient(cfg).get(10, SECONDS)

    @Bean
    fun cacheNøkkelHandler(mgr: RedisCacheManager) =
        CacheNøkkelHandler(mgr.cacheConfigurations)

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
        val MAPPER: JsonMapper = JsonMapper.builder()
            .polymorphicTypeValidator(NavPolymorphicTypeValidator()).apply {
                addModule(KotlinModule.Builder().build())
                addModule(JacksonTypeInfoAddingValkeyModule())
            }.build()
    }
}


class AllCaches(val map: Map<String, List<CachableConfig>>)