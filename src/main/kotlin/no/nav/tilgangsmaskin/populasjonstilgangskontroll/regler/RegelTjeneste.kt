package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import org.springframework.stereotype.Service

@Service
class RegelTjeneste(private val motor: RegelMotor, private val brukerTjeneste: BrukerTjeneste, private val ansattTjeneste: AnsattTjeneste, private val errorHandler: RegelExceptionHandler)  {


    fun sjekkTilgang(ansattId: NavId, brukerId: Fødselsnummer) =
        runCatching {
            motor.eksekver(brukerTjeneste.bruker(brukerId), ansattTjeneste.ansatt(ansattId))
        }.getOrElse {
            errorHandler.håndter(ansattId, brukerId, it)
        }
}

