package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringSjekker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@Timed
class RegelTjeneste(private val motor: RegelMotor, private val brukerTjeneste: BrukerTjeneste, private val ansattTjeneste: AnsattTjeneste, private val sjekker: OverstyringSjekker)  {

    private val log = LoggerFactory.getLogger(javaClass)

    fun kompletteRegler(ansattId: AnsattId, brukerId: BrukerId) =
        with(brukerTjeneste.bruker(brukerId)) {
            runCatching {
                motor.kompletteRegler(ansattTjeneste.ansatt(ansattId), this)
            }.getOrElse {
                sjekker.sjekk(ansattId, brukerId, historiskeIdentifikatorer, it)
            }
        }

    fun kjerneregler(ansattId: AnsattId, brukerId: BrukerId) =
        motor.kjerneregler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.bruker(brukerId))

    fun bulkRegler(ansattId: AnsattId, specs: List<RegelSpec>) =
        runCatching {
            motor.bulkRegler(ansattTjeneste.ansatt(ansattId), specs(specs))
        }.getOrElse {
            if (it is BulkRegelException) {
                filtrerOverstyrte(ansattId, it.exceptions)
            }
        }

    private fun filtrerOverstyrte(ansattId: AnsattId, opprinnelige: List<RegelException>) {
        with(opprinnelige.toMutableList()) {
            removeIf {
                it.regel.erOverstyrbar && sjekker.erOverstyrt(ansattId, it.brukerId)
            }.also {
                if (it) {
                    log.info("Fjernet $${opprinnelige.size - size} exception grunnet overstyrte regler for $ansattId")
                }
                else {
                    log.info("Ingen overstyrte regler for $ansattId")
                }
            }
            if (isNotEmpty()) {
                throw BulkRegelException(ansattId, this)
            }
        }
    }

    private fun specs(specs: List<RegelSpec>): List<Pair<Bruker, RegelSett.RegelType>> {
        val brukereMap = brukerTjeneste.brukere(specs.map { it.brukerId }).associateBy { it.brukerId }
        return specs.map { brukereMap[it.brukerId]!! to it.type }
    }
}


