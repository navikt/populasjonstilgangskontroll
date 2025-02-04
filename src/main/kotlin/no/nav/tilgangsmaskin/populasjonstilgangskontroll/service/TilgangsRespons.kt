package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId

data class TilgangsRespons(val kandidat: Fødselsnummer, val saksbehandler: NavId, val tilgang: Boolean, val begrunnelse: Begrunnelse? = null) {
    data class Begrunnelse(val begrunnelse: String, val kode: String, val overstyrbar: Boolean)
}

