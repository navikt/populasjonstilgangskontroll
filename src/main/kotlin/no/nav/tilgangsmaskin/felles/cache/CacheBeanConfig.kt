package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.MeterRegistry
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter.nonLockingRedisCacheWriter
import org.springframework.data.redis.config.RedisListenerConfigurer
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisMessageConverters
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.http.MediaType.APPLICATION_OCTET_STREAM
import org.springframework.messaging.Message
import org.springframework.messaging.converter.AbstractMessageConverter
import tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule.Builder
import kotlin.text.Charsets.UTF_8

@Configuration(proxyBeanMethods = true)
@ConditionalOnGCP
@NoCoverageAnalysis
class CacheBeanConfig(private val cf: RedisConnectionFactory,
                      private val meterRegistry: MeterRegistry,
                      private val errorHandler: CacheErrorHandler,
                      private vararg val cfgs: CachableRestConfig) : CachingConfigurer, RedisListenerConfigurer {


    override fun errorHandler() =
        errorHandler

    override fun configureMessageConverters(builder: RedisMessageConverters.Builder) {
        builder.addCustomConverter(CacheNøkkelMessageConverter())
    }


    @Bean
    override fun cacheManager() =
        RedisCacheManager.builder(nonLockingRedisCacheWriter(cf))
            .withInitialCacheConfigurations(cfgs.associate {
                it.navn to cacheConfig(it)
            }).enableStatistics()
            .build()


    @Bean
    fun cacheHealthIndicator(pingable: CachePingable) =
        PingableHealthIndicator(pingable)


    private fun cacheConfig(cfg: CachableRestConfig) =
        defaultCacheConfig()
            .entryTtl(cfg.varighet)
            .serializeKeysWith(fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(fromSerializer(
                ResilientValkeySerializer(GenericJacksonJsonRedisSerializer(VALKEY_MAPPER), meterRegistry))).apply {
                if (!cfg.cacheNulls) disableCachingNullValues()
            }

    companion object {
        val VALKEY_MAPPER = JsonMapper.builder().polymorphicTypeValidator(NavPolymorphicTypeValidator()).apply {
            enable(INCLUDE_SOURCE_IN_LOCATION)
            addModules(Builder().build(),JacksonTypeInfoAddingValkeyModule())
        }.build()
    }
}


class CacheNøkkelMessageConverter : AbstractMessageConverter(APPLICATION_OCTET_STREAM) {
    override fun supports(clazz: Class<*>) =
        clazz == CacheNøkkel::class.java

    override fun convertFromInternal(message: Message<*>, targetClass: Class<*>, conversionHint: Any?) =
        (message.payload as? ByteArray)
            ?.takeIf { targetClass == CacheNøkkel::class.java }
            ?.toString(UTF_8)
            ?.let(::CacheNøkkel)
}