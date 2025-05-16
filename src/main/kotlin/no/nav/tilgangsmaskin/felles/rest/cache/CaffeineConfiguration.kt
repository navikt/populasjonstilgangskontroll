package no.nav.tilgangsmaskin.felles.rest.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import no.nav.boot.conditionals.ConditionalOnGCP
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


     private fun caffeine() = Caffeine.newBuilder().recordStats()


    @Bean
     override fun cacheManager() =
        CaffeineCacheManager("pdl","graph","skjerming","overstyring").apply {
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