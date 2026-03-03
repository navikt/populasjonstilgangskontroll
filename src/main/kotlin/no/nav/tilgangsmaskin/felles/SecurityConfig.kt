package no.nav.tilgangsmaskin.felles

import no.nav.boot.conditionals.ConditionalOnNotProd
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .sessionManagement { it.sessionCreationPolicy(STATELESS) }
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/monitoring/**",
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/dev/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { it.jwt { } }
            .build()

    @Bean
    @ConditionalOnNotProd
    fun devSecurityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .securityMatcher("/dev/**")
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .csrf { it.disable() }
            .build()

    @Bean
    fun oauth2AuthorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientRepository: OAuth2AuthorizedClientRepository,
    ): OAuth2AuthorizedClientManager {
        val provider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .refreshToken()
            .build()
        return DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository).also {
            it.setAuthorizedClientProvider(provider)
        }
    }
}

