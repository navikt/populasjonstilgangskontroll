package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import org.springframework.stereotype.Service


@Service
class TilgangsService {

    fun validerTilgang(brukerIdent: String): Boolean {

        return if (brukerIdent == "12345678911") {
            true
        } else false
    }
}