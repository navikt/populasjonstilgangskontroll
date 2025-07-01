package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.pluralize
import no.nav.tilgangsmaskin.regler.motor.*
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.BulkRespons.BulkResultat
import no.nav.tilgangsmaskin.tilgang.BulkRespons.BulkResultat.Companion.ok
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service
import kotlin.collections.map
import kotlin.time.measureTime

@Service
class RegelTjeneste(
    private val motor: RegelMotor,
    private val brukerTjeneste: BrukerTjeneste,
    private val ansattTjeneste: AnsattTjeneste,
    private val overstyringTjeneste: OverstyringTjeneste) {
    private val log = getLogger(javaClass)

    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "komplett"])
    fun kompletteRegler(ansattId: AnsattId, brukerId: String) {
        val elapsedTime = measureTime {
            log.info("Sjekker ${KOMPLETT_REGELTYPE.beskrivelse} for $ansattId og ${brukerId.maskFnr()}")
            val bruker = brukerTjeneste.brukerMedNærmesteFamilie(brukerId)
            runCatching {
                motor.kompletteRegler(ansattTjeneste.ansatt(ansattId), bruker)
            }.getOrElse {
                if (overstyringTjeneste.erOverstyrt(ansattId,bruker.brukerId)) {
                    Unit
                }
                else throw it
            }
        }
        log.info("Tid brukt på komplett regelsett for $ansattId og ${brukerId.maskFnr()}: ${elapsedTime.inWholeMilliseconds}ms")
    }

    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "kjerne"])
    fun kjerneregler(ansattId: AnsattId, brukerId: String) =
        motor.kjerneregler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.brukerMedUtvidetFamilie(brukerId))

    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "bulk"])
    fun bulkRegler(ansattId: AnsattId, idOgType: Set<BrukerIdOgRegelsett>): BulkRespons {
        log.debug("Kjører bulk regler for $ansattId og $idOgType")
        val ansatt = ansattTjeneste.ansatt(ansattId)
        val brukere = idOgType.brukerOgRegelsett()
        return with(motor.bulkRegler(ansatt, brukere))  {
            val godkjente = godkjente(ansatt, this)
            val avviste = avviste(ansatt, godkjente, this, brukere)
            val ikkeFunnet = ikkeFunnet(idOgType, this)
            BulkRespons(ansattId, godkjente + avviste + ikkeFunnet).also {
                log.info("Bulk respons for $it")
            }
        }
    }

    private operator fun Set<BrukerIdOgRegelsett>.minus(funnet: Set<Bulk>) = filterNot { it.brukerId in funnet.map { it.brukerId } }.toSet()

    private fun ikkeFunnet(oppgitt: Set<BrukerIdOgRegelsett>, funnet: Set<Bulk>) =
        (oppgitt - funnet)
            .map {
                BulkResultat(it.brukerId, NOT_FOUND)
            }.toSet()

    private fun avviste(ansatt: Ansatt, godkjente: Set<BulkResultat>, resultater: Set<Bulk>, brukere: Set<BrukerOgRegelsett>) = resultater
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
        val overstyrte = overstyringTjeneste.overstyringer(ansatt.ansattId,resultater.map { it.brukerId })
            .map { brukerId -> ok(brukerId) }
        val godkjente =  resultater
            .filter { it.status.is2xxSuccessful }
            .map { bruker -> ok(bruker.brukerId) }
        return (godkjente + overstyrte).toSet()
    }


    private fun Set<BrukerIdOgRegelsett>.brukerOgRegelsett() =
        with(associate { it.brukerId.verdi to it.type }) {
            log.debug("Slår opp {} {}", "bruker".pluralize(keys,"e"), keys.map { it.maskFnr() })
            val brukere = brukerTjeneste.brukere(keys)
            brukere.map {
                BrukerOgRegelsett(it, this[it.brukerId.verdi] ?: KOMPLETT_REGELTYPE)
            }.toSet()
        }

    private fun Set<BrukerOgRegelsett>.finnBruker(brukerId: BrukerId)  = first { it.bruker.brukerId == brukerId }.bruker
}