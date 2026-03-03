package no.nav.tilgangsmaskin.felles

import com.nimbusds.jose.jwk.RSAKey
import no.nav.boot.conditionals.ConditionalOnLocalOrTest
import no.nav.boot.conditionals.ConditionalOnNotProd
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    @Order(1)
    @ConditionalOnLocalOrTest
    fun localSecurityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()

    @Bean
    @Order(2)
    @ConditionalOnNotProd
    fun devSecurityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .securityMatcher("/dev/**")
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .csrf { it.disable() }
            .build()

    @Bean
    @Order(10)
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
    @ConditionalOnMissingBean(ClientRegistrationRepository::class)
    fun emptyClientRegistrationRepository() = ClientRegistrationRepository { _: String -> null as ClientRegistration? }

    @Bean
    @ConditionalOnProperty("azure.app.jwk")
    fun oauth2AuthorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientRepository: OAuth2AuthorizedClientRepository,
        @Value("\${azure.app.jwk}") jwkJson: String,
    ): OAuth2AuthorizedClientManager {
        val jwk = RSAKey.parse(jwkJson)

        val tokenResponseClient = RestClientClientCredentialsTokenResponseClient()
        tokenResponseClient.addParametersConverter(
            NimbusJwtClientAuthenticationParametersConverter { _ -> jwk }
        )

        val provider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials { it.accessTokenResponseClient(tokenResponseClient) }
            .refreshToken()
            .build()

        return DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository).also {
            it.setAuthorizedClientProvider(provider)
        }
    }
}
