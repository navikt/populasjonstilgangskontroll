package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.TilgangControllerBase.Companion.PROD_BASE_PATH
import no.nav.tilgangsmaskin.tilgang.TilgangControllerBase.Companion.UNPROTECTED_ENDPOINTS
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor.authorizationFailureHandler
import org.springframework.security.oauth2.client.web.client.support.OAuth2RestClientHttpServiceGroupConfigurer.from
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer

@Configuration
class OAuth2SecurityBeanConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity) =
        http.authorizeHttpRequests { requests ->
                requests.requestMatchers("${PROD_BASE_PATH}/**").authenticated()
                requests.requestMatchers("/${DEV}/**").permitAll()
                requests.requestMatchers(*UNPROTECTED_ENDPOINTS).permitAll()
                requests.anyRequest().denyAll()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { it.jwtAuthenticationConverter(OAuth2LoggingJwtAuthenticationConverter()) }
                oauth2.authenticationEntryPoint(HttpStatusEntryPoint(UNAUTHORIZED))
            }
            .requestCache { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(STATELESS) }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .logout { it.disable() }
            .build()

    @Bean
    fun oauth2GroupConfigurer(manager: OAuth2AuthorizedClientManager) =
        RestClientHttpServiceGroupConfigurer { groups ->
            from(manager).configureGroups(groups)
            groups.forEachClient { _, builder ->
                builder.requestInterceptors {
                    it.addFirst(OAuth2DownstreamUriCapturingInterceptor())
                }
            }
        }

    @Bean
    fun oauth2AuthorizationFailureHandler(service: OAuth2AuthorizedClientService) =
        OAuth2LoggingAuthorizationFailureHandler(authorizationFailureHandler(service))

    @Bean
    fun oauth2AuthorizationSuccessHandler(service: OAuth2AuthorizedClientService) =
        OAuth2LoggingAuthorizationSuccessHandler(service) { client, principal, _ ->
            service.saveAuthorizedClient(client, principal)
        }

    @Bean
    fun authorizedClientManager(repo: ClientRegistrationRepository, service: OAuth2AuthorizedClientService, failureHandler: OAuth2AuthorizationFailureHandler, successHandler: OAuth2AuthorizationSuccessHandler) =
        AuthorizedClientServiceOAuth2AuthorizedClientManager(
            repo, service).apply {
            setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build())
            setAuthorizationSuccessHandler(successHandler)
            setAuthorizationFailureHandler(failureHandler)
        }
}
