package no.nav.tilgangsmaskin.felles.cache

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@EnableCaching
@TestConfiguration
abstract class CacheTestConfig(vararg cacheNames: String) {
    private val names = cacheNames

    @Bean
    fun cacheManager(): CacheManager = ConcurrentMapCacheManager(*names)

    @Primary
    @Bean
    fun cacheOperations(cacheManager: CacheManager): CacheOperations = ConcurrentMapCacheOperations(cacheManager)
}

