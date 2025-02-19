package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import org.springframework.stereotype.Service

@Service
class RegelTjeneste(private val motor: RegelMotor, private val brukerTjeneste: BrukerTjeneste, private val ansattTjeneste: AnsattTjeneste, private val errorHandler: RegelExceptionHandler)  {


    fun alleRegler(ansattId: NavId, brukerId: Fødselsnummer) =
        runCatching {
            motor.eksekverAlleRegler(brukerTjeneste.bruker(brukerId), ansattTjeneste.ansatt(ansattId))
        }.getOrElse {
            errorHandler.håndter(ansattId, brukerId, it)
        }

    fun kjerneregler(ansattId: NavId, brukerId: Fødselsnummer) = motor.eksekverKjerneregler(brukerTjeneste.bruker(brukerId), ansattTjeneste.ansatt(ansattId))
}

