package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.rest.TokenType
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component

@Component("tokenTypeAuthorization")
class OAuth2TokenTypeAuthorization(private val token: Token) {
    fun require(type: TokenType): Boolean {
        if (token.type != type) throw AccessDeniedException(mismatch(type, token.type))
        return true
    }
    companion object {
        fun mismatch(required: TokenType, actual: TokenType) =
            "Forventet ${required.name}-token, fikk ${actual.name}"

    }
}
