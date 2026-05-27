package no.nav.tilgangsmaskin.tilgang

import no.nav.tilgangsmaskin.tilgang.TokenType.Companion.from
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException


@Component
class TokenTypeGuard(private val token: Token) {
    fun krev(forventet: TokenType, uri: String) =
        with(from(token)) {
            if (this != forventet) {
                throw ResponseStatusException(FORBIDDEN, "Forventet token type $forventet for $uri, fikk $this")
            }
        }
}

