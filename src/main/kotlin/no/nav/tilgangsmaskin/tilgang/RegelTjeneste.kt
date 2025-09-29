package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
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

    @WithSpan
    fun kjerneregler(ansattId: AnsattId, brukerId: String) =
        motor.kjerneregler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.brukerMedNærmesteFamilie(brukerId))

    @WithSpan
    fun bulkRegler(ansattId: AnsattId, idOgType: Set<BrukerIdOgRegelsett>): AggregertBulkRespons {
        val (respons, elapsedTime) = measureTimedValue {
            log.debug("Bulk regler for {} med {} ident(er)", ansattId, idOgType.size)
            val ansatt = ansattTjeneste.ansatt(ansattId)
            val brukere = idOgType.brukerOgRegelsett()
            val resultater = motor.bulkRegler(ansatt, brukere).also {
                log.debug("Bulk resultater {}", it)
            }
            val godkjente = godkjente(ansatt, resultater).also {
                log.debug("Bulk godkjente {}", it)
            }
            val avviste = avviste(ansatt, godkjente, resultater, brukere).also {
                log.debug("Bulk avviste {}", it)
            }
            val ikkeFunnet = ikkeFunnet(idOgType, resultater).also {
                log.debug("Bulk ikke funnet {}", it)
            }

            AggregertBulkRespons(ansattId, godkjente + avviste + ikkeFunnet).also {
                log.info("Bulk respons (${it.godkjente.size} godkjent(e), ${it.avviste.size} avvist(e), ${it.avviste.size} ikke funnet) for $ansattId er $it ")
            }
        }
        log.info("Tid brukt på bulk med størrelse ${idOgType.size} for $ansattId: ${elapsedTime.inWholeMilliseconds}ms")
        return respons
    }

    private operator fun Set<BrukerIdOgRegelsett>.minus(funnet: Set<BulkResultat>) = filterNot { brukerIdOgRegelsett ->
        brukerIdOgRegelsett.brukerId in (funnet.map { it.bruker.historiskeIds} + funnet.map { it.bruker.oppslagId })
    }

    private fun ikkeFunnet(oppgitt: Set<BrukerIdOgRegelsett>, funnet: Set<BulkResultat>) =
        buildSet {
            for (item in (oppgitt - funnet)) {
                add(EnkeltBulkRespons(item.brukerId, NOT_FOUND))
            }
        }

    private fun avviste(ansatt: Ansatt, godkjente: Set<EnkeltBulkRespons>, resultater: Set<BulkResultat>, brukere: Set<BrukerOgRegelsett>) =
        buildSet {
        val godkjenteIds = buildSet { godkjente.forEach { add(it.brukerId) } }
        for (resultat in resultater) {
            log.trace("Bulk Sjekker overstyring for avvist {}", resultat)
            if (resultat.status == FORBIDDEN && resultat.bruker.oppslagId !in godkjenteIds) {
                log.trace("Bulk resultat {} har ingen overstyring", resultat)
                add(EnkeltBulkRespons(RegelException(ansatt, brukere.finnBruker(resultat.bruker.oppslagId), resultat.regel!!, status = resultat.status)))
            }
        }
    }

    private fun godkjente(ansatt: Ansatt, resultater: Set<BulkResultat>) =
        buildSet {
            val (godkjente, avviste) = resultater.partition { it.status.is2xxSuccessful }
            godkjente.forEach { add(ok(it.bruker.oppslagId)) }
            overstyringTjeneste
                .overstyringer(ansatt.ansattId, avviste.map { it.bruker.brukerId })
                .forEach { add(ok(it.verdi)) }
        }


    private fun Set<BrukerIdOgRegelsett>.brukerOgRegelsett() =
        with(associate { it.brukerId to it }) {
            log.debug("Slår opp keys {}", keys)
            val brukere = brukerTjeneste.brukere(keys)
            log.debug("Fant {} av {} brukere ved oppslag for keys {}", brukere, keys.size, keys)
            brukere.map { bruker ->
                val idOgType = this[bruker.oppslagId] ?: throw IllegalStateException("Bruker ${bruker.brukerId} har ikke oppslagId")
                BrukerOgRegelsett(bruker, idOgType.type)
            }.toSet()
        }

    private fun Set<BrukerOgRegelsett>.finnBruker(oppslagId: String)  = first { it.bruker.oppslagId == oppslagId }.bruker
}