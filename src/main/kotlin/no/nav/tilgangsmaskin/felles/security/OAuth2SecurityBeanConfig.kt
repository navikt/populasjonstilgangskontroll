package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.rest.PROD_BASE_PATH
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.POST
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.config.observation.SecurityObservationSettings
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor.authorizationFailureHandler
import org.springframework.security.oauth2.client.web.client.support.OAuth2RestClientHttpServiceGroupConfigurer.from
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer


private val UNPROTECTED_ENDPOINTS = arrayOf("/$DEV/**", "/swagger-ui/**", "/v3/api-docs/**", "/monitoring/**")

@Configuration
@EnableMethodSecurity
class OAuth2SecurityBeanConfig {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authMgr: AuthorizationManager<RequestAuthorizationContext>,
        deniedHandler: AccessDeniedHandler,
        entryPoint: AuthenticationEntryPoint) =
        http.authorizeHttpRequests { requests ->
            requests.requestMatchers(POST, "$PROD_BASE_PATH/overstyr").access(authMgr)
            requests.requestMatchers( *UNPROTECTED_ENDPOINTS).permitAll()
            requests.anyRequest().authenticated()
        }
            .exceptionHandling {
                it.accessDeniedHandler(deniedHandler)
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt {
                    it.jwtAuthenticationConverter(OAuth2LoggingJwtAuthenticationConverter())
                }
                oauth2.authenticationEntryPoint(entryPoint)
            }
            .statelessApiDefaults()
            .build()

    @Bean
    fun securityObservationSettings()  =
        SecurityObservationSettings.withDefaults().shouldObserveRequests(false)
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
    fun oauth2AuthorizedClientManager(repo: ClientRegistrationRepository, service: OAuth2AuthorizedClientService, successHandler: OAuth2AuthorizationSuccessHandler, failureHandler: OAuth2AuthorizationFailureHandler) =
        AuthorizedClientServiceOAuth2AuthorizedClientManager(
            repo, service).apply {
            setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build())
            setAuthorizationSuccessHandler(successHandler)
            setAuthorizationFailureHandler(failureHandler)
        }

    private fun HttpSecurity.statelessApiDefaults() =
        requestCache { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(STATELESS) }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .logout { it.disable() }
}
