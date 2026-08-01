package no.nav.tilgangsmaskin.felles.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.client.support.OAuth2RestClientHttpServiceGroupConfigurer
import org.springframework.security.web.SecurityFilterChain

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
        OAuth2RestClientHttpServiceGroupConfigurer.from(manager)

}
