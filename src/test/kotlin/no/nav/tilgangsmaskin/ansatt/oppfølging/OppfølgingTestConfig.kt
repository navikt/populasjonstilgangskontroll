package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.ConcurrentMapCacheOperations
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppfølgingTestConfig {
    @Bean
    fun cacheManager() =
        ConcurrentMapCacheManager(OPPFØLGING)
    @Bean
    fun cacheOperations(mgr: CacheManager): CacheOperations = ConcurrentMapCacheOperations(mgr)
}