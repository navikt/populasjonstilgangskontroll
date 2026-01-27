package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.CONSUMER_ID
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.mask
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.withMDC
import no.nav.tilgangsmaskin.regler.motor.Regel.Companion.INGEN_REGEL_TAG
import no.nav.tilgangsmaskin.regler.motor.Regel.Companion.regelTag
import no.nav.tilgangsmaskin.tilgang.Token
import no.nav.tilgangsmaskin.tilgang.Token.Companion.tokenTag
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class RegelMotorLogger(private val registry: MeterRegistry, private val token: Token, private val teller: EvalueringTypeTeller, private val auditor: Auditor = Auditor()) {

    private val log = getLogger(javaClass)

    private fun  bulkHistogram() =  DistributionSummary
        .builder("bulk.histogram")
        .description("Histogram av bulk-størrelse")
        .baseUnit("størrelse")
        .publishPercentileHistogram(true)
        .tags("system", token.system)
        .serviceLevelObjectives(1.0,2.0,5.0,10.0, 20.0, 50.0, 100.0)
        .register(registry)


    fun avvist(ansatt: Ansatt, bruker: Bruker, regelSett: RegelSett, regel: Regel,type: EvalueringType) =
        withMDC(Pair(BESLUTNING, regel.kode),Pair(REGELSETT, regelSett.type.beskrivelse),Pair(OPPSLAGTYPE, type.name)) {
            log.info("Tilgang avvist av regel '${regel.kortNavn}'. (${regel.begrunnelse}) for ${ansatt.ansattId} for ${bruker.brukerId} ${konsument()}")
            auditor.info("Tilgang til ${bruker.oppslagId} med GT '${bruker.geografiskTilknytning}' avvist av regel '${regel.kortNavn}' for ${ansatt.ansattId} med gruppetilhørigheter '${ansatt.grupper.map { it.displayName }}' ${konsument()}")
            teller.tell(TILGANG_AVVIST_TAG, beskrivelseTag(regelSett),regelTag(regel),tokenTag(token),evaltypeTag(type))
        }

    fun ok(ansatt: Ansatt, bruker: Bruker,regelSett: RegelSett, type: EvalueringType) =
        withMDC(Pair(BESLUTNING, OK),Pair(REGELSETT, regelSett.type.beskrivelse),Pair(OPPSLAGTYPE, type.name)) {
            log.info("${regelSett.beskrivelse} ga tilgang for ${ansatt.ansattId} ${konsument()}")
            auditor.info("${regelSett.beskrivelse} ga tilgang til ${bruker.oppslagId} for ${ansatt.ansattId} ${konsument()}")
            teller.tell(TILGANG_AKSEPTERT_TAG, beskrivelseTag(regelSett),INGEN_REGEL_TAG,tokenTag(token),evaltypeTag(type))
        }


    fun trace(message: String) = log.trace(message)

    fun evaluerer(ansatt: Ansatt, bruker: Bruker, regel: Regel,type: EvalueringType)  {
        log.trace("Evaluerer regel: '{}' for {}  og {} for {}", regel.kortNavn, ansatt.ansattId, bruker.oppslagId.mask(),type.name)
    }

    fun tellBulkSize(size: Int) =   bulkHistogram().record(size.toDouble())

    companion object   {
        private fun konsument(): String = MDC.get(CONSUMER_ID)?.let { "fra $it" } ?: "(fra uautentisert konsument)"
        private fun evaltypeTag(type: EvalueringType) = Tag.of(OPPSLAGTYPE, type.name.lowercase())
        private fun beskrivelseTag(regelsett: RegelSett) = Tag.of(BESKRIVELSE, regelsett.beskrivelse)
        private val TILGANG_AKSEPTERT_TAG = Tag.of(RESULTAT, OK)
        private val TILGANG_AVVIST_TAG = Tag.of(RESULTAT, AVVIST)
        private const val BESKRIVELSE = "type"
        private const val OPPSLAGTYPE = "oppslagtype"
        private const val REGELSETT = "regelsett"
        private const val RESULTAT = "resultat"
        private const val BESLUTNING = "beslutning"
        private const val OK = "TILGANG_OK"
        private const val AVVIST = "TILGANG_AVVIST"
    }
}



