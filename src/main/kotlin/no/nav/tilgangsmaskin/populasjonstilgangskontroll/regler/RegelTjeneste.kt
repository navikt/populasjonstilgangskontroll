package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import org.springframework.stereotype.Service

@Service
class RegelTjeneste(private val motor: RegelMotor, private val kandidatTjeneste: KandidatTjeneste, private val saksbehandlerTjeneste: SaksbehandlerTjeneste)  {

    fun sjekkTilgang(saksbehandlerId: NavId, kandidatId: Fødselsnummer) =
        motor.vurderTilgang(kandidatTjeneste.kandidat(kandidatId), saksbehandlerTjeneste.saksbehandler(saksbehandlerId))
}

