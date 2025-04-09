package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.boot.conditionals.ConditionalOnProd
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
@ConditionalOnProd
class CaffeineConfiguration : CachingConfigurer {
    private val log = LoggerFactory.getLogger(javaClass)


    override fun cacheManager()   =
        CaffeineCacheManager().apply {
            setCaffeine(
                Caffeine.newBuilder()
                .recordStats()
                .removalListener {
                        key, value, cause -> log.trace("Cache removal key={}, value={}, cause={}", key, value, cause) })
        }


    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class)
            append(method.name)
            params.forEach { append(it) }
        }
    }
}