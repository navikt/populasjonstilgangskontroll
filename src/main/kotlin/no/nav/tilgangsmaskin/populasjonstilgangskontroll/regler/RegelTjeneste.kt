package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringSjekker
import org.springframework.stereotype.Service

@Service
class RegelTjeneste(private val motor: RegelMotor, private val brukerTjeneste: BrukerTjeneste, private val ansattTjeneste: AnsattTjeneste, private val overstyringSjekker: OverstyringSjekker)  {


    fun alleRegler(ansattId: AnsattId, brukerId: BrukerId) =
        runCatching {
            motor.alleRegler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.bruker(brukerId))
        }.getOrElse {
            overstyringSjekker.sjekk(ansattId, brukerId, it)
        }

    fun kjerneregler(ansattId: AnsattId, brukerId: BrukerId) =
        motor.kjerneregler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.bruker(brukerId))
}

