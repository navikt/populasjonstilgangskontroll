package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId

interface TilgangsTjeneste {
   fun harTilgang(sakebehandler: NavId, kandidat: Fødselsnummer): TilgangsRespons
}
