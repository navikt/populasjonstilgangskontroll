package no.nav.tilgangsmaskin.felles.rest.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringEntity.Companion.OVERSTYRING
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
@ConditionalOnGCP
class CaffeineConfiguration : CachingConfigurer {
    private val log = LoggerFactory.getLogger(javaClass)


     private fun caffeine() = Caffeine.newBuilder().recordStats().removalListener {
        key: Any?, _: Any?, cause: RemovalCause ->
         log.info("Cache entry removed: key={}, cause={}", key, cause)
     }


    @Bean
     override fun cacheManager() =
        CaffeineCacheManager(PDL,GRAPH,SKJERMING,OVERSTYRING).apply {
            setCaffeine(caffeine())
        }


    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class)
            append(method.name)
            params.forEach { append(it) }
        }
    }
}