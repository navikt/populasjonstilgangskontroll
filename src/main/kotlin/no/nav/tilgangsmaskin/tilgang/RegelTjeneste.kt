package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgType
import no.nav.tilgangsmaskin.regler.motor.Regel
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service
import kotlin.time.measureTime
import  no.nav.tilgangsmaskin.tilgang.BulkResultater.BulkResultat
import org.springframework.http.HttpStatus

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
            val bruker = brukere.brukerMedNærmesteFamilie(brukerId)
            runCatching {
                motor.kompletteRegler(ansatte.ansatt(ansattId), bruker)
            }.getOrElse {
                if (overstyring.erOverstyrt(ansattId,bruker.brukerId)) {
                    Unit
                }
                else throw it
            }
        }
        log.info("Tid brukt på komplett regelsett for $ansattId og ${brukerId.maskFnr()}: ${elapsedTime.inWholeMilliseconds}ms")
    }

    fun kjerneregler(ansattId: AnsattId, brukerId: String) =
        motor.kjerneregler(ansatte.ansatt(ansattId), brukere.brukerMedUtvidetFamilie(brukerId))

    fun bulkRegler(ansattId: AnsattId, idOgType: Set<BrukerIdOgType>): BulkResultater {
        val ansatt = ansatte.ansatt(ansattId)
        val brukere = idOgType.brukerOgType()
        val resultater = motor.bulkRegler(ansatt, brukere)
        val godkjente = godkjente(resultater)
        val avviste = avviste(resultater, ansattId, brukere, ansatt)
        val ikkeFunnet = ikkeFunnet(idOgType, resultater)

        return BulkResultater(ansattId, godkjente + avviste + ikkeFunnet)
    }

    private fun ikkeFunnet(idOgType: Set<BrukerIdOgType>, resultater: Set<Triple<BrukerId, HttpStatus, Regel?>>) = idOgType
        .map { it.brukerId }
        .filterNot { it in resultater.map { it.first }.toSet() }
        .map { BulkResultat(it, NOT_FOUND) }
        .toSet()

    private fun avviste(resultater: Set<Triple<BrukerId, HttpStatus, Regel?>>, ansattId: AnsattId, brukere: Set<BrukerOgType>, ansatt: Ansatt) = resultater
        .filter { it.second == FORBIDDEN && !overstyring.erOverstyrt(ansattId, it.first) }
        .map { avvist ->
            val bruker = brukere.first { it.bruker.brukerId == avvist.first }.bruker
            val e = RegelException(ansatt, bruker, avvist.third!!, status = avvist.second)
            BulkResultat(e.bruker.brukerId, e.status, e.body)
        }.toSet()

    private fun godkjente(resultater: Set<Triple<BrukerId, HttpStatus, Regel?>>) = resultater
        .filter { it.second == NO_CONTENT }
        .map {
            BulkResultat(it.first, NO_CONTENT)
        }.toSet()

    data class BrukerOgType(val bruker: Bruker, val type: RegelType)
    data class BulkRegelResultat(val brukerId: BrukerId, val status: HttpStatus, val regel: Regel?)

    private fun Set<BrukerIdOgType>.brukerOgType(): Set<BrukerOgType> =
        mapNotNull { spec ->
            brukere.brukere(*map { it.brukerId.verdi }.toTypedArray())
                .associateBy { it.brukerId.verdi }[spec.brukerId.verdi]?.let { bruker ->
                BrukerOgType(bruker, spec.type)
            }
        }.toSet()
}