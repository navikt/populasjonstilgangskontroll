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


    @Bean
     fun caffeine() = Caffeine.newBuilder().recordStats()
        .removalListener<String, Any> { key: String?, _, cause: RemovalCause ->
            log.debug("Cache entry with key {} was removed due to {}", key, cause)
        }
    override fun keyGenerator() = KeyGenerator { target, method, params ->
        buildString {
            append(target::class)
            append(method.name)
            params.forEach { append(it) }
        }
    }
}