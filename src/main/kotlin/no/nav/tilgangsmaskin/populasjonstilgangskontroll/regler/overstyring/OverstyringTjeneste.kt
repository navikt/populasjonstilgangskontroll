package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import org.springframework.stereotype.Component

@Component
class OverstyringTjeneste {
    fun harOverstyrtTilgang(id: NavId, fødselsnummer: Fødselsnummer) : Boolean {
        return false  // TODO
    }
}