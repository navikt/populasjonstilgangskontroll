package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.midlertidig

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import org.springframework.stereotype.Component

@Component
class MidlertidigTilgangTjeneste {
    fun harMidlertidigTilgang(id: NavId, fødselsnummer: Fødselsnummer) : Boolean {
        return false
    }
}