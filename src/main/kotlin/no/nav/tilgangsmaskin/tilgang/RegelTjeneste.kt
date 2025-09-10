package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import io.opentelemetry.instrumentation.annotations.WithSpan
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
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons.Companion.ok
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service
import kotlin.collections.map
import kotlin.time.measureTimedValue
import kotlin.time.measureTime

@Service
class RegelTjeneste(
    private val motor: RegelMotor,
    private val brukerTjeneste: BrukerTjeneste,
    private val ansattTjeneste: AnsattTjeneste,
    private val overstyringTjeneste: OverstyringTjeneste) {
    private val log = getLogger(javaClass)

    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "komplett"])
        @WithSpan
    fun kompletteRegler(ansattId: AnsattId, brukerId: String) {
        val elapsedTime = measureTime {
            log.info("Sjekker ${KOMPLETT_REGELTYPE.beskrivelse} for $ansattId og ${brukerId.maskFnr()}")
            val bruker = brukerTjeneste.brukerMedNærmesteFamilie(brukerId)
            runCatching {
                motor.kompletteRegler(ansattTjeneste.ansatt(ansattId), bruker)
            }.getOrElse {
                if (overstyringTjeneste.erOverstyrt(ansattId, bruker.brukerId) && it is RegelException) {
                    log.trace("Overstyring registrert for {} og {}", ansattId, brukerId.maskFnr(), it)
                }
                else {
                    log.warn("Feil ved kjøring av komplette regler for $ansattId og ${brukerId.maskFnr()}", it)
                    throw it
                }
            }
        }
        log.info("Tid brukt på komplett regelsett for $ansattId og ${brukerId.maskFnr()}: ${elapsedTime.inWholeMilliseconds}ms")
    }

    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "kjerne"])
    @WithSpan
    fun kjerneregler(ansattId: AnsattId, brukerId: String) =
        motor.kjerneregler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.brukerMedNærmesteFamilie(brukerId))

    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "bulk"])
    @WithSpan
    fun bulkRegler(ansattId: AnsattId, idOgType: Set<BrukerIdOgRegelsett>): AggregertBulkRespons {
        val (respons, elapsedTime) = measureTimedValue {
            log.debug("Kjører bulk regler for {} med {} ident(er)", ansattId, idOgType.size)
            val ansatt = ansattTjeneste.ansatt(ansattId)
            val brukere = idOgType.brukerOgRegelsett()
            val resultater = motor.bulkRegler(ansatt, brukere)
            val godkjente = godkjente(ansatt, resultater)
            val avviste = avviste(ansatt, godkjente, resultater, brukere)
            val ikkeFunnet = ikkeFunnet(idOgType, resultater)
            AggregertBulkRespons(ansattId, godkjente + avviste + ikkeFunnet).also {
                log.info("Bulk respons (${it.godkjente.size} godkjent(e), ${it.avviste.size} avvist(e), ${it.avviste.size} ikke funnet) for $ansattId er $it ")
            }
        }
        log.info("Tid brukt på bulk med størrelse ${idOgType.size} for $ansattId: ${elapsedTime.inWholeMilliseconds}ms")
        return respons
    }

    private operator fun Set<BrukerIdOgRegelsett>.minus(funnet: Set<BulkResultat>) = filterNot { it.brukerId in funnet.map { it.brukerId } }

    private fun ikkeFunnet(oppgitt: Set<BrukerIdOgRegelsett>, funnet: Set<BulkResultat>) = buildSet {
        for (item in (oppgitt - funnet)) {
            add(EnkeltBulkRespons(item.brukerId, NOT_FOUND))
        }
    }

    private fun avviste(ansatt: Ansatt, godkjente: Set<EnkeltBulkRespons>, resultater: Set<BulkResultat>, brukere: Set<BrukerOgRegelsett>) =
        buildSet {
        val godkjenteIds = buildSet { godkjente.forEach { add(it.brukerId) } }
        for (resultat in resultater) {
            if (resultat.status == FORBIDDEN && resultat.brukerId !in godkjenteIds) {
                add(EnkeltBulkRespons(RegelException(ansatt, brukere.finnBruker(resultat.bruker.brukerId), resultat.regel!!, status = resultat.status)))
            }
        }
    }

    private fun godkjente(ansatt: Ansatt, resultater: Set<BulkResultat>) : Set<EnkeltBulkRespons> {

        val (success, fail) = resultater.partition { it.status.is2xxSuccessful }
        val ids = success.map { it.brukerId } +
                overstyringTjeneste.overstyringer(ansatt.ansattId, fail.map { it.bruker.brukerId }).map { it.verdi }
        return ids.map(::ok).toSet()
    }


    private fun Set<BrukerIdOgRegelsett>.brukerOgRegelsett() =
        with(associate { it.brukerId to it }) {
            log.debug("Slår opp {} {}", "bruker".pluralize(keys,"e"), keys.map { it.maskFnr() })
            val brukere = brukerTjeneste.brukere(keys)
            brukere.map { bruker ->
                val idOgType = this[bruker.brukerId.verdi] ?: this[bruker.aktørId?.verdi] ?: BrukerIdOgRegelsett(bruker.brukerId.verdi)
                BrukerOgRegelsett(idOgType.brukerId, bruker, idOgType.type)
            }.toSet()
        }

    private fun Set<BrukerOgRegelsett>.finnBruker(brukerId: BrukerId)  = first { it.bruker.brukerId == brukerId }.bruker
}