package no.nav.tilgangsmaskin.regler

import io.micrometer.core.annotation.Timed
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.BrukerOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.regler.motor.AggregertBulkRespons
import no.nav.tilgangsmaskin.regler.motor.BulkResponsAggregator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

@Service
class RegelTjeneste(
    private val motor: RegelMotor,
    private val brukerTjeneste: BrukerTjeneste,
    private val ansattTjeneste: AnsattTjeneste,
    private val enkeltTilgangTjeneste: EnkeltTilgangTjeneste,
    private val auditor: Auditor,
    private val aggregator: BulkResponsAggregator) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "komplett"])
    @WithSpan
    fun kompletteRegler(ansattId: AnsattId, brukerId: String) {
        val elapsedTime = measureTime {
            log.info("Sjekker ${KOMPLETT_REGELTYPE.beskrivelse} for $ansattId og ${brukerId.maskFnr()}")
            bruker(brukerId)?.let { bruker ->
                try {
                    motor.kompletteRegler(ansattTjeneste.ansatt(ansattId), bruker)
                } catch (e: RegelException) {
                    if (!enkeltTilgangTjeneste.harEnkeltTilgang(ansattId, bruker.brukerId)) {
                        log.trace("Tilgang avvist ved kjøring av komplette regler for {} og {}", ansattId, brukerId.maskFnr(), e)
                        throw e
                    }
                    log.trace("Enkelttilgang registrert for {} og {}", ansattId, brukerId.maskFnr(), e)
                }
            }
                ?: log.info("Komplette regler ikke kjørt for $ansattId og ${brukerId.maskFnr()} siden bruker ikke ble funnet, tilgang likevel gitt")
        }
        log.info("Tid brukt på komplett regelsett for $ansattId og ${brukerId.maskFnr()}: ${elapsedTime.inWholeMilliseconds}ms")
    }



    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "kjerne"])
    @WithSpan
    fun kjerneregler(ansattId: AnsattId, brukerId: String) =
        bruker(brukerId)?.let { bruker ->
            motor.kjerneregler(ansattTjeneste.ansatt(ansattId), bruker)
        } ?: log.info("Kjerneregler ikke kjørt for $ansattId og ${brukerId.maskFnr()} siden bruker ikke ble funnet, tilgang likevel gitt")

    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "bulk"])
    @WithSpan
    fun bulkRegler(ansattId: AnsattId, idOgType: Set<BrukerIdOgRegelsett>): AggregertBulkRespons {
        val (respons, elapsedTime) = measureTimedValue {
            log.debug("Eksekverer bulk for {} med størrelse {}", ansattId, idOgType.size)
            val ansatt = ansattTjeneste.ansatt(ansattId)
            val brukere = idOgType.brukerOgRegelsett()
            val resultater = motor.bulkRegler(ansatt, brukere)
            aggregator.aggreger(ansattId, ansatt, resultater, idOgType, brukere)
        }
        log.info("Tid brukt på bulk for $ansattId med størrelse ${idOgType.size}: ${elapsedTime.inWholeMilliseconds}ms")
        return respons
    }

    private fun Set<BrukerIdOgRegelsett>.brukerOgRegelsett() =
        with(associate { it.brukerId to it }) {
            val brukere = brukerTjeneste.brukere(keys)
            log.debug("Fant {} av {} brukere", brukere.size, keys.size)
            brukere.map { bruker ->
                val idOgType = this[bruker.oppslagId]
                BrukerOgRegelsett(bruker, idOgType!!.type)
            }.toSet()
        }

    private fun bruker(brukerId: String) =
        runCatching {
            brukerTjeneste.brukerMedNærmesteFamilie(brukerId)
        }.getOrElse { e ->
            when (e) {
                is NotFoundRestException -> {
                    auditor.info("${e.status}: Bruker med id $brukerId ikke funnet i PDL ved oppslag")
                    null
                }
                else -> {
                    log.warn("Feil ved oppslag av bruker for ${brukerId.maskFnr()}", e)
                    throw e
                }
            }
        }
}