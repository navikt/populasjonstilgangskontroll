package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import io.micrometer.observation.annotation.Observed
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.*
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.BulkResultater.BulkResultat
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service
import kotlin.time.measureTime

@Service
class RegelTjeneste(
    private val motor: RegelMotor,
    private val brukere: BrukerTjeneste,
    private val ansatte: AnsattTjeneste,
    private val overstyring: OverstyringTjeneste) {
    private val log = getLogger(javaClass)

    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "komplett"])
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

    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "kjerne"])
    fun kjerneregler(ansattId: AnsattId, brukerId: String) =
        motor.kjerneregler(ansatte.ansatt(ansattId), brukere.brukerMedUtvidetFamilie(brukerId))

    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "bulk"])
    fun bulkRegler(ansattId: AnsattId, idOgType: Set<BrukerIdOgType>): BulkResultater {
        val ansatt = ansatte.ansatt(ansattId)
        val brukere = idOgType.brukerOgType()
        val resultater = motor.bulkRegler(ansatt, brukere)
        val godkjente = godkjente(resultater)
        val avviste = avviste(resultater, ansattId, brukere, ansatt)
        val ikkeFunnet = ikkeFunnet(idOgType, resultater.map { it.first }.toSet())

        return BulkResultater(ansattId, godkjente + avviste + ikkeFunnet)
    }

    private fun ikkeFunnet(oppgitt: Set<BrukerIdOgType>, funnet: Set<BrukerId>) = oppgitt
        .map { it.brukerId }
        .filterNot { it in funnet }
        .map { BulkResultat(it, NOT_FOUND) }
        .toSet()

    private fun avviste(resultater: Set<Triple<BrukerId, HttpStatus, Regel?>>, ansattId: AnsattId, brukere: Set<BrukerOgType>, ansatt: Ansatt) = resultater
        .filter { it.second == FORBIDDEN && !overstyring.erOverstyrt(ansattId, it.first) }
        .map { avvist ->
            val bruker = brukere.finnBruker(avvist.first)
            val e = RegelException(ansatt, bruker, avvist.third!!, status = avvist.second)
            BulkResultat(e.bruker.brukerId, e.status, e.body)
        }.toSet()

    private fun godkjente(resultater: Set<Triple<BrukerId, HttpStatus, Regel?>>) = resultater
        .filter { it.second == NO_CONTENT }
        .map {
            BulkResultat(it.first, NO_CONTENT)
        }.toSet()

    fun Set<BrukerOgType>.finnBruker(brukerId: BrukerId)  = first { it.bruker.brukerId == brukerId }.bruker

    private fun Set<BrukerIdOgType>.brukerOgType(): Set<BrukerOgType> =
        mapNotNull { spec ->
            brukere.brukere(*map { it.brukerId.verdi }.toTypedArray())
                .associateBy { it.brukerId.verdi }[spec.brukerId.verdi]?.let { bruker ->
                BrukerOgType(bruker, spec.type)
            }
        }.toSet()
}