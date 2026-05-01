package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.felles.cache.ConcurrentMapCacheOperations
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.resilience.annotation.EnableResilientMethods

@TestConfiguration
@EnableCaching
@EnableResilientMethods
class SkjermingTestConfig {
    @Bean
    fun cacheManager()
    = ConcurrentMapCacheManager(SKJERMING)
    @Bean
    fun cache(mgr: CacheManager)
    = ConcurrentMapCacheOperations(mgr)
}