package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelSpec.RegelType.ALLE
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

    fun bulkRegler(ansattId: AnsattId, vararg specs: RegelSpec) {
        val (alle,kjerne) = specs.partition { it.type == ALLE }
        val ansatt = ansattTjeneste.ansatt(ansattId)
        val exceptions = mutableListOf<RegelException>()
        kjerne.forEach { eksekver({ motor.kjerneregler(ansatt, brukerTjeneste.bruker(it.brukerId)) }, exceptions) }
        alle.forEach { eksekver({ motor.alleRegler(ansatt, brukerTjeneste.bruker(it.brukerId)) }, exceptions) }
        if (exceptions.isNotEmpty()) throw BulkRegelException(exceptions)
    }

    private fun <T> eksekver(block: () -> T, exceptions: MutableList<RegelException>) {
        runCatching { block() }.getOrElse {
            if (it is RegelException) exceptions.add(it) else throw it
        }
    }
}


