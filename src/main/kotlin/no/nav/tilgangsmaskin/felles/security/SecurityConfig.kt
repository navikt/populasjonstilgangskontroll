package no.nav.tilgangsmaskin.felles.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor
import org.springframework.security.oauth2.client.web.client.support.OAuth2RestClientHttpServiceGroupConfigurer
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer
import org.springframework.web.service.annotation.HttpExchange
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method

@Configuration
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity) =
        http.csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .logout { it.disable() }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()

    @Bean
    fun oauth2GroupConfigurer(manager: OAuth2AuthorizedClientManager) =
        RestClientHttpServiceGroupConfigurer { groups ->
            OAuth2RestClientHttpServiceGroupConfigurer.from(manager).configureGroups(groups)
            groups.forEachClient { _, builder ->
                builder.requestInterceptors { interceptors ->
                    interceptors.add(0, OAuth2DownstreamUriCapturingInterceptor())
                }
            }
            groups.forEachProxyFactory { _, factory ->
                factory.httpRequestValuesProcessor { method, _, _, builder ->
                    val template = method.resolveDownstreamUrlTemplate()
                    builder.configureAttributes {
                        it[DOWNSTREAM_URL_TEMPLATE_ATTRIBUTE] = template
                    }
                }
            }
        }


    @Bean
    fun oauth2AuthorizationFailureHandler(service: OAuth2AuthorizedClientService): OAuth2AuthorizationFailureHandler =
        LoggingOAuth2AuthorizationFailureHandler(
            OAuth2ClientHttpRequestInterceptor.authorizationFailureHandler(service)
        )

    @Bean
    fun oauth2AuthorizationSuccessHandler(service: OAuth2AuthorizedClientService)  =
        LoggingOAuth2AuthorizationSuccessHandler(service) { authorizedClient, principal, _ ->
            service.saveAuthorizedClient(authorizedClient, principal)
        }

    @Bean
    fun authorizedClientManager(repo: ClientRegistrationRepository,
                                service: OAuth2AuthorizedClientService,
                                oauth2AuthorizationFailureHandler: OAuth2AuthorizationFailureHandler,
                                oauth2AuthorizationSuccessHandler: OAuth2AuthorizationSuccessHandler) =
        AuthorizedClientServiceOAuth2AuthorizedClientManager(
            repo, service
        ).apply {
            setAuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build()
            )
            setAuthorizationSuccessHandler(oauth2AuthorizationSuccessHandler)
            setAuthorizationFailureHandler(oauth2AuthorizationFailureHandler)
        }

    private fun Method.resolveDownstreamUrlTemplate(): String {
        val classPath = declaringClass.exchangeUrl()
        val methodPath = exchangeUrl()

        if (methodPath.startsWith("http://") || methodPath.startsWith("https://")) return methodPath
        if (classPath.isBlank()) return methodPath
        if (methodPath.isBlank()) return classPath

        return "${classPath.trimEnd('/')}/${methodPath.trimStart('/')}"
    }

    private fun AnnotatedElement.exchangeUrl() =
        AnnotatedElementUtils.findMergedAnnotation(this, HttpExchange::class.java)?.url.orEmpty()

    companion object {
        const val DOWNSTREAM_URL_TEMPLATE_ATTRIBUTE = "downstream.url.template"
    }
}
