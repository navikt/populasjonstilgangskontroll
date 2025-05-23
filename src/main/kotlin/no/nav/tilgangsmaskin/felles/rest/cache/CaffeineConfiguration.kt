package no.nav.tilgangsmaskin.felles.rest.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.boot.conditionals.ConditionalOnProd
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit.HOURS

@Configuration
@EnableCaching
@ConditionalOnProd
class CaffeineConfiguration(private vararg val cfgs: CachableRestConfig) : CachingConfigurer {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    override fun cacheManager() = CaffeineCacheManager().apply {
        cfgs.forEach { registerCustomCache(it.navn, cache(it)) }
    }

    private fun cache(cfg: CachableRestConfig) =
        Caffeine.newBuilder()
            .initialCapacity(cfg.initialCacheSize)
            .expireAfterAccess(cfg.expireHours, HOURS)
            .maximumSize(cfg.maxCacheSize.toLong())
            .recordStats()
            .removalListener { key: Any?, _: Any?, cause: RemovalCause ->
                log.info("${cfg.navn}: Cache innslag fjernet: nøkkel={},årsak={}", key, cause)
            }
            .build<Any, Any>()


    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class)
            append(method.name)
            params.forEach { append(it) }
        }
    }
}