package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest.TilgangsResponse
import org.springframework.stereotype.Service


@Service
class TilgangsService {

    fun validerTilgang(brukerIdent: String): Boolean {

        return if (brukerIdent == "12345678911") {
            true
        } else false
    }
    fun validerTilgangBulk(brukerIdenter: List<String>): List<TilgangsResponse> {
        return brukerIdenter.map { TilgangsResponse(it, "12345678911", validerTilgang(it), "Begrunnelse", "BegrunnelseKode", true) }
    }
}