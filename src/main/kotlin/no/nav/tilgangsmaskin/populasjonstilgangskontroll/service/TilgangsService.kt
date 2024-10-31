package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest.Begrunnelse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest.TilgangsResponse
import org.springframework.stereotype.Service


@Service
class TilgangsService {

    fun validerTilgang(brukerIdent: String, navIdent: String): Boolean {
        validereNavIdent(navIdent) //forventer navident på  (A-Z)(1-9)(0-9){5}
        return if (brukerIdent == "12345678911") {
            true
        } else false
    }

    private fun validereNavIdent(navIdent: String) {
        fun checkNavIdent(navIdent: String): Boolean {
            val regex = Regex("^[A-Z]{1}[0-9]{6}\$")
            return regex.matches(navIdent)
        }
        fun checkIfEntraId(navIdent: String): Boolean {
            val regex = Regex("^[A-Z]{1}[0-9]{6}\$")
            return regex.matches(navIdent)
        }
    }

    fun validerTilgangBulk(brukerIdenter: List<String>, navIdent: String): List<TilgangsResponse> {
        return brukerIdenter.map { TilgangsResponse(it, "12345678911", validerTilgang(it, navIdent), Begrunnelse("begrunnelse", "begrunnelse_kode", false)
            ) }
    }
}