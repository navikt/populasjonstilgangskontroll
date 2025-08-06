package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.boot.conditionals.EnvUtil.isDevOrLocal
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.slf4j.LoggerFactory.getLogger
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
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import kotlin.reflect.jvm.jvmName

@Configuration(proxyBeanMethods = true)
@EnableCaching
@ConditionalOnGCP
class ValKeyBeanConfigurer(private val cf: RedisConnectionFactory,
                           mapper: ObjectMapper,
                           private val env: Environment,
                           private vararg val cfgs: CachableRestConfig) : CachingConfigurer {


    private val log = getLogger(javaClass)

    private val valKeyMapper =
        mapper.copy().apply {
            if (isDevOrLocal(env)) {
              //  registerModule(JsonCacheableModule())
                activateDefaultTyping(polymorphicTypeValidator, EVERYTHING, PROPERTY)
             //    activateDefaultTyping(polymorphicTypeValidator, NON_FINAL_AND_ENUMS, PROPERTY)
            }
            else {
                activateDefaultTyping(polymorphicTypeValidator, EVERYTHING, PROPERTY)
            }
        }

    @Bean
    override fun cacheManager(): RedisCacheManager =
        RedisCacheManager.builder(lockingRedisCacheWriter(cf))
            .withInitialCacheConfigurations(cfgs.associate { it.navn to cacheConfig(it) })
            .enableStatistics()
            .build()

    @Bean
    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class.jvmName)
            append(":")
            append(method.name)
            append(":")
            params.forEach {
                if (it is BrukerId) {
                    log.trace("Genererer cache-nøkkel med hash")
                    append(it.hashCode().toString())
                }
                else {
                    if (it is Set<*>) {
                        log.trace("Genererer cache-nøkkel for samling: {}", it.customToString())
                        append(it.customToString())
                    }
                    else {
                        append(it)
                    }
                }
            }
        }
    }

    private fun <T> Collection<T>.customToString(): String =
        joinToString(prefix = "[", postfix = "]") {
            if (it is BrukerId) it.verdi else it.toString()
        }


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

