package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.CONSUMER_ID
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.withMDC
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class RegelMotorLogger(private val registry: MeterRegistry, private val token: Token, private val teller: EvalueringTeller) {

    private val log = getLogger(javaClass)

    private fun  bulkHistogram() =  DistributionSummary
        .builder("bulk.histogram")
        .description("Histogram av bulk-størrelse")
        .baseUnit("størrelse")
        .publishPercentileHistogram(true)
        .tags("system", token.system)
        .serviceLevelObjectives(1.0,2.0,5.0,10.0, 20.0, 50.0, 100.0)
        .register(registry)


    fun avvist(ansatt: Ansatt, bruker: Bruker, regelSett: RegelSett, regel: Regel) =
        withMDC(BESLUTNING, regel.kode) {
            log.info("Tilgang avvist av regel '${regel.kortNavn}'. (${regel.begrunnelse}) for ${ansatt.ansattId} for ${bruker.brukerId} ${konsument()}")
            teller.info("Tilgang til ${bruker.oppslagId} med GT '${bruker.geografiskTilknytning}' avvist av regel '${regel.kortNavn}' for ${ansatt.ansattId} med gruppetilhørigheter '${ansatt.grupper.map { it.displayName }}' ${konsument()}")
            teller.tell(Tags.of(RESULTAT, AVVIST, "type", regelSett.beskrivelse,"regel",regel.navn))
        }

    fun ok(ansatt: Ansatt, bruker: Bruker,regelSett: RegelSett) =
        withMDC(BESLUTNING, OK) {
            log.info("${regelSett.beskrivelse} ga tilgang for ${ansatt.ansattId} ${konsument()}")
            teller.tell(Tags.of(RESULTAT, OK, "type", regelSett.beskrivelse,"regel","-"))
            teller.info("${regelSett.beskrivelse} ga tilgang til ${bruker.oppslagId} for ${ansatt.ansattId} ${konsument()}")
        }

    private fun konsument(): String = MDC.get(CONSUMER_ID)?.let { "fra $it" } ?: "(fra uautentisert konsument)"

    fun trace(message: String) = log.trace(message)

    fun evaluerer(ansatt: Ansatt, bruker: Bruker, regel: Regel)  {
        log.trace("Evaluerer regel: '{}' for {}  og {}", regel.kortNavn, ansatt.ansattId, bruker.oppslagId.maskFnr())
    }

    fun tellBulkSize(size: Int) =   bulkHistogram().record(size.toDouble())

    companion object   {
        private const val RESULTAT = "resultat"
        private const val BESLUTNING = "beslutning"
        private const val OK = "TILGANG_OK"
        private const val AVVIST = "TILGANG_AVVIST"
    }
}



