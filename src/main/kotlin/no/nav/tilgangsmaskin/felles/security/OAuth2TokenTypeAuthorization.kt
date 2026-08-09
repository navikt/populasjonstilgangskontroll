package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.rest.TokenType
import no.nav.tilgangsmaskin.felles.rest.TokenType.CCF
import no.nav.tilgangsmaskin.felles.rest.TokenType.OBO
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component

@Component("tokenTypeAuthorization")
class OAuth2TokenTypeAuthorization(private val token: Token) {
    fun requireOBO(): Boolean {
        if (token.type != OBO) throw AccessDeniedException(forventetOBO(token.type))
        return true
    }

    fun requireCCF(): Boolean {
        if (token.type != CCF) throw AccessDeniedException(forventetCCF(token.type))
        return true
    }
    companion object {
        fun forventetOBO(type: TokenType) = "Forventet OBO-token, fikk $type"
        fun forventetCCF(type: TokenType) = "Forventet CCF-token, fikk $type"

    }
}
