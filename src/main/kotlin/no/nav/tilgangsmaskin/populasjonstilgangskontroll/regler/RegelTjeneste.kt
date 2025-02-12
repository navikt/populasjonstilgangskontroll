package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import org.springframework.stereotype.Service

@Service
class RegelTjeneste(private val motor: RegelMotor, private val brukerTjeneste: BrukerTjeneste, private val ansattTjeneste: AnsattTjeneste)  {

    fun sjekkTilgang(saksbehandlerId: NavId, kandidatId: Fødselsnummer) =
        motor.vurderTilgang(brukerTjeneste.bruker(kandidatId), ansattTjeneste.ansatt(saksbehandlerId))
}

