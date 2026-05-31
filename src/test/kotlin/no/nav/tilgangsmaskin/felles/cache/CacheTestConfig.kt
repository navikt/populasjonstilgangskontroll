package no.nav.tilgangsmaskin.felles.cache

import com.github.benmanes.caffeine.cache.Caffeine.newBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@EnableCaching
@TestConfiguration(proxyBeanMethods = false)
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
open class CacheTestConfig(private  vararg val names: String) {


    @Bean
    fun cacheManager() = CaffeineCacheManager(*names).apply {
        setCaffeine(newBuilder().maximumSize(10_000))
    }

    @Primary
    @Bean
    fun cacheOperations(mgr: CacheManager) = CaffeineCacheClient(mgr)
}

