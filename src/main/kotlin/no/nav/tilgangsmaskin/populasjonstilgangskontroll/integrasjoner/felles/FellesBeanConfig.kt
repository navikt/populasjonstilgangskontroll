package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.ClientConfigurationPropertiesMatcher
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

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
    fun oAuth2ClientRequestInterceptor(properties: ClientConfigurationProperties, service: OAuth2AccessTokenService) = LocalOAuth2ClientRequestInterceptor(properties, service)
}

class LocalOAuth2ClientRequestInterceptor(private val properties: ClientConfigurationProperties,
                                     private val service: OAuth2AccessTokenService,
                                     private val matcher: ClientConfigurationPropertiesMatcher =  object : ClientConfigurationPropertiesMatcher {}) : ClientHttpRequestInterceptor {

    private val log = LoggerFactory.getLogger(OAuth2ClientRequestInterceptor::class.java)


    override fun intercept(req: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        log.trace("Intercepting request to {}", req.uri)
        matcher.findProperties(properties, req.uri)?.let {
            log.trace("Found properties for uri {} med scope  {}", req.uri, it.scope)
            service.getAccessToken(it).access_token?.let {
                    token -> req.headers.setBearerAuth(token)
                log.trace(CONFIDENTIAL, "Finished setting access token  {} in authorization header OK for uri {}", token,req.uri)
            }
        }
        return execution.execute(req, body)
    }
    override fun toString() = "${javaClass.simpleName}  [properties=$properties, service=$service, matcher=$matcher]"

}
