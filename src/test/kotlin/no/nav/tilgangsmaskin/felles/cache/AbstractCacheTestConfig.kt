package no.nav.tilgangsmaskin.felles.cache

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean

open class AbstractCacheTestConfig(private val cache: String) {
    @Bean
    open fun cacheManager() = ConcurrentMapCacheManager(cache)

    @Bean
    open fun cache(mgr: CacheManager) : CacheOperations = ConcurrentMapCacheOperations(mgr)
}