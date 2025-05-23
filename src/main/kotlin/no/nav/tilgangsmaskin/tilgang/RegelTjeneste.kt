package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.IdOgType
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.RegelMotor.BulkRegelResult
import no.nav.tilgangsmaskin.regler.motor.RegelMotor.BulkRegelResult.*
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import kotlin.time.measureTime

@Service
@Timed
class RegelTjeneste(
    private val motor: RegelMotor,
    private val brukere: BrukerTjeneste,
    private val ansatte: AnsattTjeneste,
    private val overstyring: OverstyringTjeneste) {
    private val log = getLogger(javaClass)

    fun kompletteRegler(ansattId: AnsattId, brukerId: String) {
        val elapsedTime = measureTime {
            log.info("Sjekker ${KOMPLETT_REGELTYPE.beskrivelse} for $ansattId og ${brukerId.maskFnr()}")
            val bruker = brukere.nærmesteFamilie(brukerId)
            runCatching {
                motor.kompletteRegler(ansatte.ansatt(ansattId), bruker)
            }.getOrElse {
                overstyring.sjekk(ansattId, it)
            }
        }
        log.info("Tid brukt på komplett regelsett for $ansattId og ${brukerId.maskFnr()}: ${elapsedTime.inWholeMilliseconds}ms")
    }

    fun kjerneregler(ansattId: AnsattId, brukerId: String) =
        motor.kjerneregler(ansatte.ansatt(ansattId), brukere.utvidetFamilie(brukerId))

    fun bulkRegler(ansattId: AnsattId, idOgType: Set<IdOgType>): List<BulkRegelResult> {
        lateinit var resultater: List<BulkRegelResult>
        val elapsedTime = measureTime {
            log.info("Eksekverer bulk regler for $ansattId og ${idOgType.map { it.brukerId }.map { it.maskFnr() }}")
            resultater = motor.bulkRegler(ansatte.ansatt(ansattId), idOgType.brukerIdOgType()).map {
                when (it) {
                    is Success -> it.also {
                        log.info("Regel for ${it.brukerId} er OK")
                    }
                    is RegelFailure ->
                        if (overstyring.erOverstyrt(ansattId, it.brukerId)) {
                            Success(it.brukerId).also {
                                log.info("Regel for ${it.brukerId} er overstyrt")
                            }
                        } else {
                            log.warn("Regel for ${it.brukerId} er avvist med ${it.exception.message}")
                            it
                        }
                    is InternalError -> it.also {
                        log.error("Regel for ${it.brukerId} feilet med ${it.exception.message}")
                    }
                }
            }
        }
        log.info("Tid brukt på bulk regler for $ansattId og ${idOgType.map { it.brukerId }.map { it.maskFnr() }}: ${elapsedTime.inWholeMilliseconds}ms")
        return resultater
    }

    private fun Set<IdOgType>.brukerIdOgType() =
        mapNotNull { spec ->
            brukere.brukere(*map { it.brukerId }.toTypedArray())
                .associateBy { it.brukerId.verdi }[spec.brukerId]?.let { bruker ->
                bruker to spec.type
            }
        }.toSet()
}


