package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.rest.notifikajon.Auditor
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.CONSUMER_ID
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.tilgangsmaskin.felles.security.AuthContext
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.withMDC
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class RegelMotorLogger(private val registry: MeterRegistry,
                       private val authContext: AuthContext,
                       private val teller: EvalueringTypeTeller,
                       private val auditor: Auditor) {

    private val log = getLogger(RegelMotor::class.java)

    private val bulkHistogram by lazy {
        DistributionSummary
            .builder("bulk.histogram")
            .description("Histogram av bulk-størrelse")
            .baseUnit("størrelse")
            .publishPercentileHistogram(true)
            .tags("system", authContext.system)
            .serviceLevelObjectives(1.0, 2.0, 5.0, 10.0, 20.0, 50.0, 100.0)
            .register(registry)
    }

    fun avvist(ansatt: Ansatt, bruker: Bruker, regelSett: RegelSett, regel: Regel, type: EvalueringType) =
        withMDC(mapOf(
            BESLUTNING to regel.kode,
            USER_ID to ansatt.ansattId.verdi,
            REGELSETT to regelSett.type.beskrivelse,
            OPPSLAGTYPE to type.name
        )) {
            log.info("Tilgang avvist av regel '${regel.kortNavn}'. (${regel.begrunnelse}) for ${ansatt.ansattId} for ${bruker.brukerId} ${konsument()}")
            auditor.info("Tilgang til ${bruker.oppslagId} med GT '${bruker.geografiskTilknytning}' avvist av regel '${regel.kortNavn}' for ${ansatt.ansattId} med gruppetilhørigheter '${ansatt.grupper.map { it.displayName }}' ${konsument()}")
            teller.tell(TILGANG_AVVIST_TAG,
                regelSett.tag(),
                regel.tag(),
                authContext.tag(),
                type.tag())
        }

    fun ok(ansatt: Ansatt, bruker: Bruker, regelSett: RegelSett, type: EvalueringType) =
        withMDC(mapOf(
            BESLUTNING to OK,
            USER_ID to ansatt.ansattId.verdi,
            REGELSETT to regelSett.type.beskrivelse,
            OPPSLAGTYPE to type.name
        )) {
            log.info("Principal er ${authContext.principal()?.name ?: "uautentisert"}")
            log.info("${regelSett.beskrivelse} ga tilgang for ${ansatt.ansattId} ${konsument()}")
            auditor.info("${regelSett.beskrivelse} ga tilgang til ${bruker.oppslagId} for ${ansatt.ansattId} ${konsument()}")
            teller.tell(TILGANG_AKSEPTERT_TAG,
                regelSett.tag(),
                INGEN_REGEL_TAG,
                authContext.tag(),
                type.tag())
        }


    fun trace(message: String) = log.trace(message)

    fun tellBulkSize(size: Int) = bulkHistogram.record(size.toDouble())
    fun godkjent(ansatt: Ansatt, bruker: Bruker, regel: Regel, type: EvalueringType) {
        log.trace("Evaluert regel '{}' OK for {} for {} og {} {}",
            regel.kortNavn,
            ansatt.ansattId,
            bruker.oppslagId.maskFnr(),
            type.name,
            konsument())
    }

    companion object {
        private fun konsument(): String =
            MDC.get(CONSUMER_ID)?.let { "for konsument $it" } ?: "(for uautentisert konsument)"

        private const val REGEL = "regel"
        val INGEN_REGEL_TAG = Tag.of(REGEL, UTILGJENGELIG)
        fun Regel.tag() = Tag.of(REGEL, kortNavn)
        private fun AuthContext.tag() = Tag.of(FLOW, type.name.lowercase())
        private fun EvalueringType.tag() = Tag.of(OPPSLAGTYPE, name.lowercase())
        private fun RegelSett.tag() = Tag.of(BESKRIVELSE, beskrivelse)
        private val TILGANG_AKSEPTERT_TAG = Tag.of(RESULTAT, OK)
        private val TILGANG_AVVIST_TAG = Tag.of(RESULTAT, AVVIST)
        private const val FLOW = "flow"
        private const val BESKRIVELSE = "type"
        private const val OPPSLAGTYPE = "oppslagtype"
        private const val REGELSETT = "regelsett"
        private const val RESULTAT = "resultat"
        private const val BESLUTNING = "beslutning"
        private const val OK = "TILGANG_OK"
        private const val AVVIST = "TILGANG_AVVIST"
    }
}
