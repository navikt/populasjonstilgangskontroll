package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
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
        val godkjente = godkjente(resultater, ansattId)
        val godkjenteIds = godkjente.map { it.brukerId }.toSet()
        val avviste = avviste(resultater.filter { it.second == FORBIDDEN }.toSet(), ansattId, brukere, ansatt)
            .filterNot { it.brukerId in godkjenteIds }
            .toSet()
        val ikkeFunnet = ikkeFunnet(idOgType.map { it.brukerId }.toSet(), resultater.map { it.first }.toSet())
        return BulkResultater(ansattId, godkjente + avviste + ikkeFunnet)
    }

    private fun ikkeFunnet(oppgitt: Set<BrukerId>, funnet: Set<BrukerId>) = oppgitt.subtract(funnet).map { BulkResultat(it, NOT_FOUND) }.toSet()

    private fun avviste(resultater: Set<Triple<BrukerId, HttpStatus, Regel?>>, ansattId: AnsattId, brukere: Set<BrukerOgType>, ansatt: Ansatt) = resultater
        .map {
            with(it) {
                BulkResultat(RegelException(ansatt, brukere.finnBruker(first), third!!, status = second))
            }
        }.toSet()

    private fun godkjente(resultater: Set<Triple<BrukerId, HttpStatus, Regel?>>, ansattId: AnsattId) : Set<BulkResultat> {
        val overstyrte = resultater
            .filter {
                overstyring.erOverstyrt(ansattId, it.first)
            }
            .map {
                BulkResultat(it.first, NO_CONTENT)
            }
        val godkjente =  resultater
            .filter { it.second == NO_CONTENT }
            .map {
                BulkResultat(it.first, NO_CONTENT)
            }
        return (godkjente + overstyrte).toSet()
    }


private fun Set<BrukerOgType>.finnBruker(brukerId: BrukerId)  = first { it.bruker.brukerId == brukerId }.bruker

    private fun Set<BrukerIdOgType>.brukerOgType(): Set<BrukerOgType> =
        mapNotNull {
            with(it) {
                brukere.brukere(map { it.brukerId.verdi }.toSet())
                    .associateBy { it.brukerId.verdi }[brukerId.verdi]?.let { bruker ->
                    BrukerOgType(bruker, type)
                }
            }
        }.toSet()
}