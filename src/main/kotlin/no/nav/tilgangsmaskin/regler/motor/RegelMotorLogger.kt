package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.CONSUMER_ID
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.withMDC
import no.nav.tilgangsmaskin.felles.utils.Secure
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.stereotype.Component
import no.nav.tilgangsmaskin.tilgang.Token

@Component
class RegelMotorLogger(private val registry: MeterRegistry, private val token: Token) {

    private val log = getLogger(javaClass)
    private val avvisningTeller = AvvisningTeller(registry, token)
    private val regeltypeTeller = RegeltypeTeller(registry, token)
    private fun  bulkHistogram() =  DistributionSummary
        .builder("bulk.histogram")
        .description("Histogram av bulk-størrelse")
        .baseUnit("størrelse")
        .publishPercentileHistogram(true)
        .tags("system", token.system)
        .serviceLevelObjectives(1.0,2.0,5.0,10.0, 20.0, 50.0, 100.0)
        .register(registry)

    fun tellRegelSett(regelSett: RegelSett) = regeltypeTeller.tell(Tags.of("type",regelSett.beskrivelse, "system", token.system))

    fun avvist(ansatt: Ansatt, bruker: Bruker, regel: Regel) =
        withMDC(BESLUTNING, regel.kode) {
            val fra =  MDC.get(CONSUMER_ID)?.let { "fra $it" } ?: "(fra uautentisert konsument)"
            log.warn("Tilgang avvist av regel '${regel.kortNavn}'. (${regel.begrunnelse}) for ${ansatt.ansattId} for $bruker $fra")
            Secure.warn("Tilgang til ${bruker.oppslagId} avvist av regel '${regel.kortNavn}' for ${ansatt.ansattId} $fra")
            avvisningTeller.tell(Tags.of("navn", regel.navn))
        }

    fun ok(ansatt: Ansatt, bruker: Bruker,regelSett: RegelSett) =
        withMDC(BESLUTNING, OK) {
            val fra = MDC.get(CONSUMER_ID)?.let { "fra $it" } ?: "(fra uautentisert konsument)"
            log.info("${regelSett.beskrivelse} ga tilgang for ${ansatt.ansattId} $fra")
            Secure.info("${regelSett.beskrivelse} ga tilgang til ${bruker.oppslagId} for ${ansatt.ansattId} $fra")
        }

    fun info(message: String) = log.info(message)

    fun warn(message: String, e: Throwable? = null) = log.warn(message,e)

    fun trace(message: String) = log.trace(message)

    fun evaluerer(ansatt: Ansatt, bruker: Bruker, regel: Regel) =
        log.trace("Evaluerer regel: '{}' for {}  og {}", regel.kortNavn, ansatt.ansattId, bruker.oppslagId.maskFnr())

    fun tellBulkSize(size: Int) =   bulkHistogram().record(size.toDouble())

    companion object   {
        private const val BESLUTNING = "beslutning"
        private const val OK = "TILGANG_OK"
    }
}



