package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.MeterBinder
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.boot.conditionals.EnvUtil
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.Pingable
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter.lockingRedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ScanOptions.scanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration
import kotlin.reflect.jvm.jvmName

@Configuration
@EnableCaching
@ConditionalOnGCP
class ValkeyBeanConfiguration(private val cf: RedisConnectionFactory,
                              @Value("\${valkey.host.cache}") private val host: String,
                              @Value("\${valkey.port.cache}") private val port: String,
                              mapper: ObjectMapper,
                              private val env: Environment,
                              private vararg val cfgs: CachableRestConfig) : CachingConfigurer, Pingable {

    private val log = getLogger(javaClass)

    override val pingEndpoint  = "$host:$port"
    override val name = "ValKey Cache"

    private val valkeyMapper =
        mapper.copy().apply {
            log.info("Modules for default  mapper: ${registeredModuleIds.joinToString()}")
            if (EnvUtil.isDevOrLocal(env)) {
                registerModule(JsonCacheableModule())
            }
            activateDefaultTyping(polymorphicTypeValidator, EVERYTHING, PROPERTY)
            log.info("Modules for ValKey cache mapper: ${registeredModuleIds.joinToString()}")
        }

    @Bean
    fun valkeyCacheSizeMeterBinder(template: StringRedisTemplate) = MeterBinder { registry ->
            cfgs.forEach { cfg ->
                registry.gauge("cache.size", Tags.of("navn", cfg.navn), template) { _ ->
                    cacheSize( cfg.navn)
                }
            }
        }

    @Bean
    override fun cacheManager(): RedisCacheManager =
        RedisCacheManager.builder(lockingRedisCacheWriter(cf))
            .withInitialCacheConfigurations(cfgs.associate { it.navn to cacheConfig(it) })
            .enableStatistics()
            .build()

    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class.jvmName)
            append(":")
            append(method.name)
            append(":")
            params.forEach {
                if (it is BrukerId) {
                    append(it.verdi)
                }
                else {
                    append(it)
                }
            }
        }
    }
    override fun ping() =
        cf.connection.use {
            if (it.ping().equals("PONG", ignoreCase = true)) {
                emptyMap<String,String>()
            }
            else {
                error("$name ping failed")
            }
        }
    
    fun cacheSizes() = cfgs.associate { it.navn to "${cacheSize(it.navn).toLong()} innslag i cache"}

    private fun cacheSize(cacheName: String) =
        cf.connection.use {
            it.keyCommands()
                .scan(scanOptions().match("*$cacheName*").count(1000).build())
                .asSequence()
                .count()
                .toDouble()
        }

    private fun cacheConfig(cfg: CachableRestConfig) =
        defaultCacheConfig()
            .disableCachingNullValues()
            .entryTtl(Duration.ofHours(cfg.expireHours))
            .serializeKeysWith(fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(fromSerializer(GenericJackson2JsonRedisSerializer(valkeyMapper)))
}