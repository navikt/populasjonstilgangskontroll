package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId

interface RegelTjeneste {
   fun sjekkTilgang(sakebehandler: NavId, kandidat: Fødselsnummer)
}
