package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.CONSUMER_ID
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.withMDC
import no.nav.tilgangsmaskin.tilgang.Token
import no.nav.tilgangsmaskin.tilgang.TokenType
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class RegelMotorLogger(private val registry: MeterRegistry, private val token: Token, private val teller: EvalueringTeller,private val typeTeller: EvalueringTypeTeller) {

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
        withMDC(Pair(BESLUTNING, regel.kode),Pair(REGELSETT, regelSett.type.beskrivelse)) {
            log.info("Tilgang avvist av regel '${regel.kortNavn}'. (${regel.begrunnelse}) for ${ansatt.ansattId} for ${bruker.brukerId} ${konsument()}")
            teller.audit("Tilgang til ${bruker.oppslagId} med GT '${bruker.geografiskTilknytning}' avvist av regel '${regel.kortNavn}' for ${ansatt.ansattId} med gruppetilhørigheter '${ansatt.grupper.map { it.displayName }}' ${konsument()}")
            typeTeller.tell(TILGANG_AVVIST, beskrivelse(regelSett),INGEN_REGEL,token(token),evaltype(type))
            teller.tell(TILGANG_AVVIST, beskrivelse(regelSett),INGEN_REGEL,token(token))
        }

    fun ok(ansatt: Ansatt, bruker: Bruker,regelSett: RegelSett, type: EvalueringType) =
        withMDC(Pair(BESLUTNING, OK),Pair(REGELSETT, regelSett.type.beskrivelse)) {
            log.info("${regelSett.beskrivelse} ga tilgang for ${ansatt.ansattId} ${konsument()}")
            teller.audit("${regelSett.beskrivelse} ga tilgang til ${bruker.oppslagId} for ${ansatt.ansattId} ${konsument()}")
            teller.tell(TILGANG_AKSEPTERT, beskrivelse(regelSett),INGEN_REGEL,token(token))
            typeTeller.tell(TILGANG_AKSEPTERT, beskrivelse(regelSett),INGEN_REGEL,token(token),evaltype(type))
        }


    fun trace(message: String) = log.trace(message)

    fun evaluerer(ansatt: Ansatt, bruker: Bruker, regel: Regel,type: EvalueringType)  {
        log.trace("Evaluerer regel: '{}' for {}  og {} for {}", regel.kortNavn, ansatt.ansattId, bruker.oppslagId.maskFnr(),type.name)
    }

    fun tellBulkSize(size: Int) =   bulkHistogram().record(size.toDouble())

    companion object   {
        private fun konsument(): String = MDC.get(CONSUMER_ID)?.let { "fra $it" } ?: "(fra uautentisert konsument)"
        private fun evaltype(type: EvalueringType) = Tag.of(EVALTYPE, type.name.lowercase())
        private fun token(token: Token) = Tag.of(FLOW, TokenType.from(token).name.lowercase())
        private fun beskrivelse(regelsett: RegelSett) = Tag.of(BESKRIVELSE, regelsett.beskrivelse)
        private val TILGANG_AKSEPTERT = Tag.of(RESULTAT, OK)
        private val TILGANG_AVVIST = Tag.of(RESULTAT, AVVIST)
        private val INGEN_REGEL = Tag.of(REGEL, INGEN)
        private const val BESKRIVELSE = "type"
        private const val REGEL = "regel"
        private const val FLOW = "flow"
        private const val INGEN = "-"
        private const val EVALTYPE = "evalueringtype"
        private const val REGELSETT = "regelsett"
        private const val RESULTAT = "resultat"
        private const val BESLUTNING = "beslutning"
        private const val OK = "TILGANG_OK"
        private const val AVVIST = "TILGANG_AVVIST"
    }
}



