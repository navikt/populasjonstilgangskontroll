package no.nav.tilgangsmaskin.felles.cache

import no.nav.boot.conditionals.ConditionalOnLocalOrTest
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnLocalOrTest
class LocalCacheBeanConfig(private vararg val cfgs: CachableRestConfig) {

    @Bean
    fun cacheManager() = ConcurrentMapCacheManager(
        *cfgs.flatMap { it.caches }.map { it.name }.toTypedArray()
    )
}
