package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.rest.TokenType
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component

internal fun mismatch(required: TokenType, actual: TokenType) = "Forventet ${required.name}-token, fikk ${actual.name}"

@Component("tokenTypeAuthorization")
class OAuth2TokenTypeAuthorization(private val token: Token) {
    fun require(type: TokenType) = (token.type == type).also {
        if (!it) throw AccessDeniedException(mismatch(type, token.type))
    }
}
