package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.ConcurrentMapCacheOperations
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean

@TestConfiguration
@EnableCaching
class CacheConfig {
    @Bean
    fun cacheManager(): CacheManager = ConcurrentMapCacheManager(SkjermingConfig.SKJERMING)
    @Bean
    fun cache(cacheManager: CacheManager): CacheOperations = ConcurrentMapCacheOperations(cacheManager)
}