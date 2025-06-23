package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import io.opentelemetry.api.trace.Span
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.IdOgType
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.RegelSett.*
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.BulkResultater.BulkResultat
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpStatus.*
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

    fun bulkRegler(ansattId: AnsattId, idOgType: Set<IdOgType>): BulkResultater {
        val ansatt = ansatte.ansatt(ansattId)
        val brukere = idOgType.brukerIdOgType()
        val resultater = motor.bulkRegler(ansatt, brukere)

        val godkjente = resultater
            .filter { it.second == ACCEPTED }
            .map { BulkResultat(it.first, OK.value()) }
            .toSet()

        val avviste = resultater
            .filter { it.second == UNAUTHORIZED && !overstyring.erOverstyrt(ansattId, it.first) }
            .map { avvist ->
                val bruker = brukere.first { it.first.brukerId == avvist.first }.first
                val e = RegelException(ansatt, bruker, avvist.third!!, status = avvist.second)
                BulkResultat(e.bruker.brukerId, e.status.value(), e.body)
            }
            .toSet()

        val funnetBrukerIder = resultater.map { it.first }.toSet()
        val ikkeFunnet = idOgType
            .map { it.brukerId }
            .filterNot { it in funnetBrukerIder }
            .map { BulkResultat(it, NOT_FOUND.value()) }
            .toSet()

        return BulkResultater(ansattId, godkjente + avviste + ikkeFunnet)
    }

data class BulkResultater(val ansattId: AnsattId,val resultater: Set<BulkResultat>,val traceId: String = Span.current().spanContext.traceId) {
    data class BulkResultat(val brukerId: BrukerId, val status: Int, val body: Any? = null )
}


@ConfigurationProperties("regler")
data class RegelConfig(val toggles: Map<String,Boolean> = emptyMap()) {
    fun isEnabled(regel: String) = toggles[regel.lowercase() +".enabled"]  ?: true
}


