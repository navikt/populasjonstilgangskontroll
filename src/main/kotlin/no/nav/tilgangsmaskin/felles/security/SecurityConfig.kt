package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDLPIP
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.tilgang.TilgangControllerBase.Companion.PROD_PREFIX
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer

@Configuration
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity) =
        http.csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .logout { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("$PROD_PREFIX/**").authenticated()
                    .anyRequest().permitAll()
            }
            .oauth2ResourceServer { it.jwt { } }
            .build()

    @Bean
    fun oauth2GroupConfigurer(manager: OAuth2AuthorizedClientManager,
                              service: OAuth2AuthorizedClientService) =
        RestClientHttpServiceGroupConfigurer { groups ->
            groups.filterByName(*OAUTH2_GROUPS).forEachClient { _, builder ->
                builder.requestInterceptor(OAuth2ClientHttpRequestInterceptor(manager).apply {
                    setAuthorizationFailureHandler(
                        OAuth2ClientHttpRequestInterceptor.authorizationFailureHandler(service))
                })
            }
        }

    @Bean
    fun oauth2AuthorizedClientService(repo: ClientRegistrationRepository): OAuth2AuthorizedClientService =
        InMemoryOAuth2AuthorizedClientService(repo)

    @Bean
    fun authorizedClientManager(repo: ClientRegistrationRepository,
                                service: OAuth2AuthorizedClientService): OAuth2AuthorizedClientManager =
        AuthorizedClientServiceOAuth2AuthorizedClientManager(
            repo, service).apply {
            setAuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build())
            setAuthorizationFailureHandler(
                OAuth2ClientHttpRequestInterceptor.authorizationFailureHandler(service))
        }

    companion object {
        private val OAUTH2_GROUPS = arrayOf(SKJERMING, ENTRAPROXY, GRAPH, PDLPIP, PDLGRAPH, VERGEMÅL)
    }
}
