package no.nav.tilgangsmaskin.tilgang

import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

/**
 * Verifiserer at innkommende request bruker forventet token-flow (OBO eller CCF).
 *
 * Hvert endepunkt deklarerer eksplisitt hvilken [TokenType] det aksepterer — guarden
 * sammenligner mot det faktiske tokenet og kaster `403 Forbidden` ved mismatch.
 */
@Component
class TokenTypeGuard(private val token: Token) {

    /**
     * @throws ResponseStatusException med status 403 dersom [forventet] ikke matcher tokenets faktiske type.
     */
    fun krev(forventet: TokenType, uri: String) {
        val faktisk = TokenType.from(token)
        if (faktisk != forventet) {
            throw ResponseStatusException(FORBIDDEN, "Mismatch mellom token type $faktisk og $uri")
        }
    }
}

