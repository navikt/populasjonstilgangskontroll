package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.BrukerOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.BulkResultat
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons.Companion.ok
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Service
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

@Service
class RegelTjeneste(
    private val motor: RegelMotor,
    private val brukerTjeneste: BrukerTjeneste,
    private val ansattTjeneste: AnsattTjeneste,
    private val overstyringTjeneste: OverstyringTjeneste,
    private val auditor: Auditor = Auditor()) {
    private val log = getLogger(javaClass)

    @Timed( value = "regel_tjeneste", histogram = true, extraTags = ["type", "komplett"])
    @WithSpan
    fun kompletteRegler(ansattId: AnsattId, brukerId: String) {
        val elapsedTime = measureTime {
            log.info("Sjekker ${KOMPLETT_REGELTYPE.beskrivelse} for $ansattId og ${brukerId.maskFnr()}")
            bruker(brukerId)?.let { bruker ->
                runCatching {
                    motor.kompletteRegler(ansattTjeneste.ansatt(ansattId), bruker)
                }.getOrElse {
                    if (overstyringTjeneste.erOverstyrt(ansattId, bruker.brukerId) && it is RegelException) {
                        log.trace("Overstyring registrert for {} og {}", ansattId, brukerId.maskFnr(), it)
                    } else {
                        log.trace("Tilgang avvist ved kjøring av komplette regler for {} og {}", ansattId, brukerId.maskFnr(), it)
                        throw it
                    }
                }
            } ?: log.info("Komplette regler ikke kjørt for $ansattId og ${brukerId.maskFnr()} siden bruker ikke ble funnet, tilgang likevel gitt")
        }
        log.info("Tid brukt på komplett regelsett for $ansattId og ${brukerId.maskFnr()}: ${elapsedTime.inWholeMilliseconds}ms")
    }

    private fun bruker(brukerId: String) = runCatching {
        brukerTjeneste.brukerMedNærmesteFamilie(brukerId)
    }.getOrElse {
        if (it is NotFoundRestException) {
            auditor.info("${NOT_FOUND.name}: Bruker med id $brukerId ikke funnet i PDL ved oppslag")
            null
        } else {
            log.warn("Feil ved oppslag av bruker for ${brukerId.maskFnr()}", it)
            throw it
        }
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
            log.debug("Bulk regler for {} med {} ident(er)", ansattId, idOgType.size)
            val ansatt = ansattTjeneste.ansatt(ansattId)
            val brukere = idOgType.brukerOgRegelsett()
            val resultater = motor.bulkRegler(ansatt, brukere).also {
                log.debug("Bulk resultater {}", it)
            }
            val godkjente = godkjente(ansatt, resultater)
            val avviste = avviste(ansatt, godkjente, resultater, brukere)
            val ikkeFunnet = ikkeFunnet(idOgType, resultater)
            AggregertBulkRespons(ansattId, godkjente + avviste + ikkeFunnet)
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
                add(ok(item.brukerId))
            }
        }.also {
            if (it.isNotEmpty()) {
                auditor.info("404: Brukere med identer ${it.map { ident -> ident.brukerId }} ikke funnet i PDL ved oppslag")
                log.debug("${it.size} bulk elementer ikke funnet")
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
    }.also {
            log.debug("Bulk avviste {}", it)
        }

    private fun godkjente(ansatt: Ansatt, resultater: Set<BulkResultat>) =
        buildSet {
            val (godkjente, avviste) = resultater.partition { it.status.is2xxSuccessful }
            godkjente.forEach { add(ok(it.bruker.oppslagId)) }
            overstyringTjeneste
                .overstyringer(ansatt.ansattId, avviste.map { it.bruker.brukerId })
                .forEach { add(ok(it.verdi)) }
        }.also {
            if (it.isNotEmpty()) {
                log.debug("Bulk godkjente oppslagId(s) {}", it.map { it.brukerId })
            }
        }


    private fun Set<BrukerIdOgRegelsett>.brukerOgRegelsett() =
        with(associate { it.brukerId to it }) {
            val brukere = brukerTjeneste.brukere(keys)
            log.debug("Fant {} av {} brukere", brukere.size, keys.size)
            brukere.map { bruker ->
                val idOgType = this[bruker.oppslagId] ?: error("Bruker ${bruker.brukerId} har ikke oppslagId")
                BrukerOgRegelsett(bruker, idOgType.type)
            }.toSet()
        }

    private fun Set<BrukerOgRegelsett>.finnBruker(oppslagId: String)  = first { it.bruker.oppslagId == oppslagId }.bruker

}