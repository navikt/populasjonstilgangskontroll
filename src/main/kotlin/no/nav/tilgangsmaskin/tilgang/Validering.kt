package no.nav.tilgangsmaskin.tilgang

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

fun sjekk(predikat: Boolean, status: HttpStatus, message: String) {
    if (!predikat) throw ResponseStatusException(status, message)
}
