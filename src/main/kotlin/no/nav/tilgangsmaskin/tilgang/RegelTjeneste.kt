package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
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

    fun bulkRegler(ansattId: AnsattId, idOgType: Set<IdOgType>): List<Pair<BrukerId, Int>> {
        lateinit var resultater: List<Pair<BrukerId,Any>>
        val elapsedTime = measureTime {
            log.info("Eksekverer bulk regler for $ansattId og ${idOgType.map { it.brukerId }.map { it.maskFnr() }}")
            resultater = motor.bulkRegler(ansatte.ansatt(ansattId), idOgType.brukerIdOgType()).map { spec ->
                when (spec) {
                    is Success -> {
                        log.info("Regel for ${spec.brukerId} er OK")
                        Pair(spec.brukerId,204)
                    }
                    is RegelFailure ->
                        if (overstyring.erOverstyrt(ansattId, spec.brukerId)) {
                            log.info("Regel for ${spec.brukerId} er overstyrt")
                            Pair(spec.brukerId,spec.exception.body)
                        } else {
                            log.warn("Regel for ${spec.brukerId} er avvist med ${spec.exception.message}")
                            Pair(spec.brukerId, spec.statusCode.value())
                        }
                    is InternalError ->{
                        log.error("Regel for ${spec.brukerId} feilet med ${spec.exception.message}")
                        Pair(spec.brukerId,500)
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


