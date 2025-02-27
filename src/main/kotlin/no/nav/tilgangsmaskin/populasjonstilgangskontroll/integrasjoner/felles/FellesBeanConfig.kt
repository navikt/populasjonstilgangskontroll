package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryListener

@Configuration
class FellesBeanConfig {

    private val log = LoggerFactory.getLogger(FellesBeanConfig::class.java)

    @Bean
    fun errorMessageSource() = ReloadableResourceBundleMessageSource().apply {
            setBasename("classpath:messages")
            setDefaultEncoding("UTF-8")
        }
    @Bean
    fun fellesRetryListener() = FellesRetryListener()

    @Bean
    fun caffeine() = Caffeine.newBuilder()
        .recordStats()
        .removalListener { key: Any?, value: Any?, cause -> log.trace(CONFIDENTIAL,"Cache removal key={}, value={}, cause={}", key, value, cause)
    }.build<Any, Any>()
    @Bean
    fun oAuth2ClientRequestInterceptor(properties: ClientConfigurationProperties, service: OAuth2AccessTokenService) = OAuth2ClientRequestInterceptor(properties, service)
}

class FellesRetryListener: RetryListener {

    private val log = LoggerFactory.getLogger(FellesRetryListener::class.java)
    override fun <T : Any?, E : Throwable?> onSuccess(context: RetryContext, callback: RetryCallback<T, E>, result: T) {
        log.info("Retry success på forsøk ${context.retryCount}")
    }
    override fun <T : Any?, E : Throwable?> onError(context: RetryContext, callback: RetryCallback<T, E>, e: Throwable) {
        log.info("Retry feilet på forsøk ${context.retryCount}")
    }

    companion object    {
        const val FELLES_RETRY_LISTENER = "fellesRetryListener"
    }
}
