package no.nav.tilgangsmaskin.populasjonstilgangskontroll.config

import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.context.annotation.Configuration

@EnableJwtTokenValidation(ignore = ["org.springdoc", "org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController"])
@EnableOAuth2Client(cacheEnabled = true)
@Configuration
internal class SecurityConfig {
}