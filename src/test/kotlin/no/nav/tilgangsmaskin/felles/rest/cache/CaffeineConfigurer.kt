package no.nav.tilgangsmaskin.felles.rest.cache

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.boot.conditionals.ConditionalOnLocalOrTest
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
@ConditionalOnLocalOrTest
class CaffeineConfigurer(private vararg val cfgs: CachableRestConfig) : CachingConfigurer {

    @Bean
    override fun cacheManager() = CaffeineCacheManager().apply {
        cfgs.forEach { registerCustomCache(it.navn, cache(it)) }
    }

    private fun cache(cfg: CachableRestConfig) =
        Caffeine.newBuilder()
            .expireAfterAccess(cfg.varighet.toHours(), TimeUnit.HOURS)
            .recordStats()
            .build<Any, Any>()


    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class)
            append(method.name)
            params.forEach { append(it) }
        }
    }
}