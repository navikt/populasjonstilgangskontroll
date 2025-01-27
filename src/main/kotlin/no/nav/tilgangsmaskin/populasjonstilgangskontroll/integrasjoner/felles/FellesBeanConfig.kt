package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
@Configuration
class FellesBeanConfig {

    //@Bean
   // fun restClientCustomizer(properties: ClientConfigurationProperties, service: OAuth2AccessTokenService) = RestClientCustomizer {
   //   it.requestFactory(HttpComponentsClientHttpRequestFactory())
   // }

    @Bean
    fun oAuth2ClientRequestInterceptor(properties: ClientConfigurationProperties, service: OAuth2AccessTokenService) = OAuth2ClientRequestInterceptor(properties, service)

}