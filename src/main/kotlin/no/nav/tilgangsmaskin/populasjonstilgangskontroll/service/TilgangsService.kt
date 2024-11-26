package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl.PdlGraphClient
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl.PdlGraphResponse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest.Begrunnelse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest.TilgangsResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient


@Service
class TilgangsService(pdlGraphClient: PdlGraphClient) {
    val pdlGraphClient = pdlGraphClient

    fun validerTilgang(brukerIdent: String, navIdent: String): Boolean {
        validereGyldigFnr(brukerIdent)
        hentPersonFraPdl(brukerIdent)
        return if (brukerIdent == "12345678911") {
            true
        } else false
    }

    private fun validereGyldigFnr(fnr: String) { //støtter kun 11 siffer
        if (fnr.length != 11) {
            throw IllegalArgumentException("Fødselsnummer må være 11 siffer")
        }

    }

    fun validerTilgangBulk(brukerIdenter: List<String>, navIdent: String): List<TilgangsResponse> {
        return brukerIdenter.map { TilgangsResponse(it, "12345678911", validerTilgang(it, navIdent), Begrunnelse("begrunnelse", "begrunnelse_kode", false)
            ) }
    }



    private fun hentPersonFraPdl(brukerIdent: String): PdlGraphResponse {
       return pdlGraphClient.hentPerson(brukerIdent)
    }


}
