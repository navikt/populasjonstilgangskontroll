package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractPingableHealthIndicator
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryListener
import org.springframework.web.client.RestClient.Builder

@Configuration
class SkjermingClientBeanConfig {

    @Bean
    @Qualifier(SKJERMING)
    fun skjermingRestClient(b: Builder, cfg: SkjermingConfig, oAuth2ClientRequestInterceptor: OAuth2ClientRequestInterceptor) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptors { it.addFirst(oAuth2ClientRequestInterceptor)
            }.build()

    @Bean
    fun skjermingHealthIndicator(a: SkjermingRestClientAdapter) = object : AbstractPingableHealthIndicator(a) {}

    @Bean//(name = ["skjermingRetryListener"])
    fun skjermingRetryListener() = SkjermingRetryListener()
}

class SkjermingRetryListener: RetryListener {

    private val log = LoggerFactory.getLogger(SkjermingRetryListener::class.java)
    override fun <T : Any?, E : Throwable?> onSuccess(context: RetryContext, callback: RetryCallback<T, E>, result: T) {
        log.trace("Retry success på forsøk ${context.retryCount}")
    }
    override fun <T : Any?, E : Throwable?> onError(context: RetryContext, callback: RetryCallback<T, E>, e: Throwable) {
        log.trace("Retry feilet på forsøk ${context.retryCount}")
    }
}