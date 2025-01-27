package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.ClientConfigurationPropertiesMatcher
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Component

@Configuration
class FellesBeanConfig {

    @Bean
    fun restClientCustomizer(properties: ClientConfigurationProperties, service: OAuth2AccessTokenService) = RestClientCustomizer {
      it.requestFactory(HttpComponentsClientHttpRequestFactory())
    }

    @Bean
    fun oAuth2ClientRequestInterceptor(properties: ClientConfigurationProperties, service: OAuth2AccessTokenService) = OAuth2ClientRequestInterceptor(properties, service)

    @Component
    class LoggingOAuth2ClientRequestInterceptor(private val properties: ClientConfigurationProperties,
                                         private val service: OAuth2AccessTokenService,
                                         private val matcher: ClientConfigurationPropertiesMatcher =  object : ClientConfigurationPropertiesMatcher {}) : ClientHttpRequestInterceptor {
         private val log = LoggerFactory.getLogger(this::class.java)
        override fun intercept(req: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
            log.info("intercepting request to ${req.uri}")
            matcher.findProperties(properties, req.uri)?.let {
                log.info("found properties $it for ${req.uri}")
                service.getAccessToken(it).access_token?.let { token -> req.headers.setBearerAuth(token) }
            }
            return execution.execute(req, body)
        }
    }
}