package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import org.springframework.stereotype.Service

@Service
class RegelTjeneste(private val motor: RegelMotor, private val brukerTjeneste: BrukerTjeneste, private val ansattTjeneste: AnsattTjeneste, private val errorHandler: RegelExceptionHandler)  {


    fun alleRegler(ansattId: AnsattId, brukerId: Fødselsnummer) =
        runCatching {
            motor.alleRegler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.bruker(brukerId))
        }.getOrElse {
            errorHandler.håndter(ansattId, brukerId, it)
        }

    fun kjerneregler(ansattId: AnsattId, brukerId: Fødselsnummer) =
        motor.kjerneregler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.bruker(brukerId))
}

