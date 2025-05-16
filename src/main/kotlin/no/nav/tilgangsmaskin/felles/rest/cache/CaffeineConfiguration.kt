package no.nav.tilgangsmaskin.felles.rest.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import com.github.benmanes.caffeine.cache.RemovalListener
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit.HOURS

@Configuration(proxyBeanMethods = true)
@EnableCaching
@ConditionalOnGCP
class CaffeineConfiguration(private val pdl: PdlConfig, private val entra: EntraConfig, private val skjerming: SkjermingConfig) : CachingConfigurer {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
     override fun cacheManager() =
       CaffeineCacheManager().apply {
           registerCustomCache(PDL, cache(PDL ,pdl))
           registerCustomCache(GRAPH,cache(GRAPH, entra))
           registerCustomCache(SKJERMING,cache(SKJERMING, skjerming))
       }

    private fun cache(navn: String, cfg: CachableRestConfig) =
        Caffeine.newBuilder()
            .initialCapacity(cfg.initialCacheSize)
            .expireAfterAccess(cfg.expireHours, HOURS)
            .maximumSize(cfg.maxCacheSize.toLong())
            .recordStats()
            .removalListener(RemovalListener { key: Any?, value: Any?, cause: RemovalCause ->
                log.info("$navn: Cache innslag fjernet: nøkkel={},årsak={}", key, cause)
            })
            .build<Any, Any>()


    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class)
            append(method.name)
            params.forEach { append(it) }
        }
    }
}