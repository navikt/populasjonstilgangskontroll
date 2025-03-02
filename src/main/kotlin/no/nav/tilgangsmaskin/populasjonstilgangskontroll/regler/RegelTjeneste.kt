package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelSpec.RegelType.ALLE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringSjekker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RegelTjeneste(private val motor: RegelMotor, private val brukerTjeneste: BrukerTjeneste, private val ansattTjeneste: AnsattTjeneste, private val overstyringSjekker: OverstyringSjekker)  {

    private val log = LoggerFactory.getLogger(javaClass)

    fun alleRegler(ansattId: AnsattId, brukerId: BrukerId) =
        runCatching {
            motor.alleRegler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.bruker(brukerId))
        }.getOrElse {
            overstyringSjekker.sjekk(ansattId, brukerId, it)
        }

    fun kjerneregler(ansattId: AnsattId, brukerId: BrukerId) =
        motor.kjerneregler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.bruker(brukerId))

    fun bulkRegler(ansattId: AnsattId, vararg specs: RegelSpec) {
        log.trace("Eksekverer ${specs.size} regler for ansatt $ansattId")
        val (alle,kjerne) = specs.partition { it.type == ALLE }
        log.trace("Eksekverer ${alle.size} komplette regler for ansatt $ansattId")
        log.trace("Eksekverer ${kjerne.size} kjerneregler for ansatt $ansattId")

        val ansatt = ansattTjeneste.ansatt(ansattId)
        val exceptions = mutableListOf<RegelException>()
        kjerne.forEach { eksekver({ motor.kjerneregler(ansatt, brukerTjeneste.bruker(it.brukerId)) }, exceptions) }.also { log.trace("Antall feil etter kjerneregler er ${exceptions.size}") }
        alle.forEach { eksekver({ motor.alleRegler(ansatt, brukerTjeneste.bruker(it.brukerId)) }, exceptions) }.also { log.trace("Antall feil etter alle regler er ${exceptions.size}") }
        if (exceptions.isNotEmpty()) throw BulkRegelException(exceptions).also {
            log.trace("Det er ${exceptions.size} feil i bulk-regler for ansatt $ansattId")
        }
    }

    private fun <T> eksekver(block: () -> T, exceptions: MutableList<RegelException>) {
        runCatching { block() }.getOrElse {
            if (it is RegelException) exceptions.add(it) else throw it
        }
    }
}


