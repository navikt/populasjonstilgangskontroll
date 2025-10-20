package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.Tags.empty
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.CONSUMER_ID
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.withMDC
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class RegelMotorLogger(private val registry: MeterRegistry, private val token: Token, private val auditor: Auditor) {

    private val log = getLogger(javaClass)
    private val avvisningTeller = AvvisningTeller(registry, token)
    private val regeltypeTeller = RegeltypeTeller(registry, token)
    private val evalueringTeller = EvalueringTeller(registry, token)

    private fun  bulkHistogram() =  DistributionSummary
        .builder("bulk.histogram")
        .description("Histogram av bulk-størrelse")
        .baseUnit("størrelse")
        .publishPercentileHistogram(true)
        .tags("system", token.system)
        .serviceLevelObjectives(1.0,2.0,5.0,10.0, 20.0, 50.0, 100.0)
        .register(registry)

    fun tellRegelSett(regelSett: RegelSett) = regeltypeTeller.tell(Tags.of("type",regelSett.beskrivelse, "system", token.system))

    fun avvist(ansatt: Ansatt, bruker: Bruker, regelSett: RegelSett, regel: Regel) =
        withMDC(BESLUTNING, regel.kode) {
            val fra =  MDC.get(CONSUMER_ID)?.let { "fra $it" } ?: "(fra uautentisert konsument)"
            log.info("Tilgang avvist av regel '${regel.kortNavn}'. (${regel.begrunnelse}) for ${ansatt.ansattId} for ${bruker.brukerId} $fra")
            auditor.info("Tilgang til ${bruker.oppslagId} med GT '${bruker.geografiskTilknytning}' avvist av regel '${regel.kortNavn}' for ${ansatt.ansattId}  med gruppetilhørigheter '${ansatt.grupper.map { it.displayName }}' $fra")
            tellEvaluering(AVVIST, regelSett, Tags.of("navn", regel.navn))
            avvisningTeller.tell(Tags.of("navn", regel.navn))
        }

    fun ok(ansatt: Ansatt, bruker: Bruker,regelSett: RegelSett) =
        withMDC(BESLUTNING, OK) {
            val fra = MDC.get(CONSUMER_ID)?.let { "fra $it" } ?: "(fra uautentisert konsument)"
            log.info("${regelSett.beskrivelse} ga tilgang for ${ansatt.ansattId} $fra")
            tellEvaluering(OK, regelSett)
            auditor.info("${regelSett.beskrivelse} ga tilgang til ${bruker.oppslagId} for ${ansatt.ansattId} $fra")
        }

    private fun tellEvaluering(status: String, regelSett: RegelSett, tags: Tags = empty()) =
        evalueringTeller.tell(Tags.of("resultat", status,"type",regelSett.beskrivelse,"system", token.system).and(tags))

    fun info(message: String) = log.info(message)

    fun trace(message: String) = log.trace(message)

    fun evaluerer(ansatt: Ansatt, bruker: Bruker, regel: Regel)  {
        log.trace("Evaluerer regel: '{}' for {}  og {}", regel.kortNavn, ansatt.ansattId, bruker.oppslagId.maskFnr())
    }

    fun tellBulkSize(size: Int) =   bulkHistogram().record(size.toDouble())

    companion object   {
        private const val BESLUTNING = "beslutning"
        private const val OK = "TILGANG_OK"
        private const val AVVIST = "TILGANG_AVVIST"

    }
}



