package no.nav.tilgangsmaskin.felles.security

import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt

class OAuth2AudienceValidator(private val audiences: List<String>) : OAuth2TokenValidator<Jwt> {
    override fun validate(token: Jwt) =
        if (audiences.isEmpty() || token.audience.orEmpty().any { it in audiences }) {
            OAuth2TokenValidatorResult.success()
        } else {
            OAuth2TokenValidatorResult.failure(OAuth2Error("invalid_token", "Missing expected audience", null))
        }
}