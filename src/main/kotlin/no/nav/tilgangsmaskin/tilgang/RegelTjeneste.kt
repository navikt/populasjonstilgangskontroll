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
import no.nav.tilgangsmaskin.tilgang.BulkRespons.BulkResultat
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service
import kotlin.collections.map
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
    fun bulkRegler(ansattId: AnsattId, idOgType: Set<BrukerIdOgRegelsett>): BulkRespons {
        val ansatt = ansatte.ansatt(ansattId)
        val brukere = idOgType.brukerOgType()
        with(motor.bulkRegler(ansatt, brukere))  {
            val godkjente = godkjente(ansatt, this)
            val avviste = avviste(ansatt, godkjente, this, brukere)
            val ikkeFunnet = ikkeFunnet(idOgType, this)
            return BulkRespons(ansattId, godkjente + avviste + ikkeFunnet)
        }
    }

    private fun ikkeFunnet(oppgitt: Set<BrukerIdOgRegelsett>, funnet: Set<Bulk>) =
       (oppgitt.map { it.brukerId } - funnet.map { it.brukerId })
           .map {
               BulkResultat(it, NOT_FOUND)
           }.toSet()

    private fun avviste(ansatt: Ansatt, godkjente: Set<BulkResultat>, resultater: Set<Bulk>, brukere: Set<BrukerOgType>) = resultater
        .filter {
            it.status == FORBIDDEN
        }
        .filterNot {
            it.brukerId in godkjente.map { it.brukerId
            }
        }
        .map {
            with(it) { BulkResultat(RegelException(ansatt, brukere.finnBruker(brukerId), regel!!, status = status)) }
        }
        .toSet()

    private fun godkjente(ansatt: Ansatt, resultater: Set<Bulk>) : Set<BulkResultat> {
        val overstyrte = resultater
            .filter {
                overstyring.erOverstyrt(ansatt.ansattId, it.brukerId)
            }
            .map {
                BulkResultat(it.brukerId, NO_CONTENT)
            }
        val godkjente =  resultater
            .filter { it.status == NO_CONTENT }
            .map {
                BulkResultat(it.brukerId, NO_CONTENT)
            }
        return (godkjente + overstyrte).toSet()
    }


private fun Set<BrukerOgType>.finnBruker(brukerId: BrukerId)  = first { it.bruker.brukerId == brukerId }.bruker

    private fun Set<BrukerIdOgRegelsett>.brukerOgType(): Set<BrukerOgType> =
        mapNotNull {
            with(it) {
                brukere.brukere(map { it.brukerId.verdi }.toSet())
                    .associateBy { it.brukerId.verdi }[brukerId.verdi]?.let { bruker ->
                    BrukerOgType(bruker, type)
                }
            }
        }.toSet()
}