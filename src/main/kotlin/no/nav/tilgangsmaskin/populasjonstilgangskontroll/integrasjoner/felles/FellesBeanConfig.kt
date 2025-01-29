package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
@Configuration
class FellesBeanConfig {

    @Bean
    fun caffeine() = Caffeine.newBuilder().recordStats()
    @Bean
    fun oAuth2ClientRequestInterceptor(properties: ClientConfigurationProperties, service: OAuth2AccessTokenService) = OAuth2ClientRequestInterceptor(properties, service)
}
