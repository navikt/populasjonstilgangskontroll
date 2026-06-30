package no.nav.tilgangsmaskin.tilgang

import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException


@Component
class TokenTypeGuard(val token: Token) {
    fun krev(forventet: TokenType, uri: String) =
        with(token.type) {
            if (this != forventet) {
                throw ResponseStatusException(FORBIDDEN, "Forventet token type $forventet for $uri, fikk $this")
            }
        }
}
