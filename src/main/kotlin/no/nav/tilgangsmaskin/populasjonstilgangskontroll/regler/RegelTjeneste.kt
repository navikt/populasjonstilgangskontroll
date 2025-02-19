package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import org.springframework.stereotype.Service

@Service
class RegelTjeneste(private val motor: RegelMotor, private val brukerTjeneste: BrukerTjeneste, private val ansattTjeneste: AnsattTjeneste, private val errorHandler: RegelExceptionHandler)  {


    fun sjekkTilgang(saksbehandlerId: NavId, brukerId: Fødselsnummer) =
        runCatching {
            motor.vurderTilgang(brukerTjeneste.bruker(brukerId), ansattTjeneste.ansatt(saksbehandlerId))
        }.getOrElse {
            errorHandler.håndter(saksbehandlerId, brukerId, it)
        }
}

