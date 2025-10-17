package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.instrument.Tags
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.tilgangsmaskin.felles.rest.ValidOverstyring
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.TokenTypeTeller
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringData
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.Token.Companion.AAD_ISSUER
import org.jboss.logging.MDC
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.MULTI_STATUS
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

@SecurityScheme(bearerFormat = "JWT", name = "bearerAuth", scheme = "bearer", type = HTTP)
@ProtectedRestController(value = ["/api/v1"], issuer = AAD_ISSUER, claimMap = [])
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "TilgangController", description = "Denne kontrolleren skal brukes i produksjon")
class TilgangController(
    private val regelTjeneste: RegelTjeneste,
    private val overstyringTjeneste: OverstyringTjeneste,
    private val token: Token,
    private val teller: TokenTypeTeller) {

    private val log = getLogger(javaClass)

    @PostMapping("komplett")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    @Operation(summary = "Evaluer et komplett regelsett for en bruker, forutsetter OBO-token")
    fun kompletteRegler(@RequestBody brukerId: String,request: HttpServletRequest) = enkeltOppslag({token.ansattId!!}, {token.erObo}, brukerId, KOMPLETT_REGELTYPE,request.requestURI)

    @PostMapping("/ccf/komplett/{ansattId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    @Operation(summary = "Evaluer et komplett regelsett for en bruker, forutsetter CCF-token")
    fun kompletteReglerCCF(@PathVariable ansattId: AnsattId,@RequestBody brukerId: String,request: HttpServletRequest) = enkeltOppslag({ansattId}, {token.erCC}, brukerId, KOMPLETT_REGELTYPE, request.requestURI)

    @PostMapping("kjerne")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    @Operation(summary = "Evaluer et kjerneregelsett for en bruker, forutsetter OBO-token")
    fun kjerneregler(@RequestBody brukerId: String, request: HttpServletRequest) = enkeltOppslag({token.ansattId!!}, {token.erObo}, brukerId, KJERNE_REGELTYPE, request.requestURI)


    @PostMapping("/ccf/kjerne/{ansattId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    @Operation(summary = "Evaluer et komplett regelsett for en bruker, forutsetter CCF-token")
    fun kjerneReglerCCF(@PathVariable ansattId: AnsattId,@RequestBody brukerId: String,request: HttpServletRequest) = enkeltOppslag({ansattId}, {token.erCC}, brukerId, KJERNE_REGELTYPE,request.requestURI)


    @PostMapping("overstyr")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse
    @Operation(summary = "Overstyr regler for en bruker",
        description =  """Setter overstyring for en bruker, slik at den kan saksbehandles selv om tilgang opprinnelig avslås.
    BrukerId må være gyldig og finnes i PDL. Kjerneregelsettet vil bli kjørt før overstyring, og hvis de feiler vil overstyring ikke bli gjort.
    Overstyring vil gjelde frem til og med utløpsdatoen.""")
    fun overstyr(@RequestBody @Valid @ValidOverstyring data: OverstyringData) = overstyringTjeneste.overstyr(token.ansattId!!, data)

    @PostMapping("bulk/obo")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    @Operation(summary = "Kjør bulkregler for en ansatt",
        description = "Dette endepunktet er kun tilgjengelig for obo flow. " +
                "Det evaluerer regler for en ansatt mot et sett av brukerId-er og regeltyper. Om ingen regeltype oppgis, evalueres det komplette regelsettet")
    fun bulkOBO(@RequestBody  specs: Set<BrukerIdOgRegelsett>,request: HttpServletRequest) =
        bulkOppslag({token.ansattId!!},{token.erObo}, specs,request.requestURI)

    @PostMapping("bulk/obo/{regelType}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    @Operation(summary = "Kjør bulkregler for en ansatt",
        description = "Dette endepunktet er kun tilgjengelig for obo flow. " +
                "Det evaluerer regler for en ansatt mot et sett av brukerId-er med gitt regeltype")
    fun bulkOBOForRegelType(@PathVariable regelType: RegelType, @RequestBody brukerIds: Set<BrukerId>,request: HttpServletRequest) =
        bulkOppslag({token.ansattId!!},{token.erObo},brukerIds.map { BrukerIdOgRegelsett(it.verdi,regelType) }.toSet(),request.requestURI)

    @PostMapping("bulk/ccf/{ansattId}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    @Operation(summary = "Kjør bulkregler for en ansatt",
        description = "Dette endepunktet er kun tilgjengelig for client credentials flow. " +
                "Det evaluerer regler for en ansatt mot et sett av brukerId-er og regeltyper. Om ingen regeltype oppgis, evalueres det komplette regelsettet")
    fun bulkCCF(@PathVariable ansattId: AnsattId, @RequestBody specs: Set<BrukerIdOgRegelsett>,request: HttpServletRequest) =
        bulkOppslag({ansattId},{token.erCC}, specs,request.requestURI)

    @PostMapping("bulk/ccf/{ansattId}/{regelType}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    @Operation(summary = "Kjør bulkregler for en ansatt",
        description = "Dette endepunktet er kun tilgjengelig for client credentials flow. " +
                "Det evaluerer regler for en ansatt mot et sett av brukerId-er med gitt regeltype")
    fun bulkCCFForRegelType(@PathVariable ansattId: AnsattId, @PathVariable regelType: RegelType, @RequestBody brukerIds: Set<BrukerId>, request: HttpServletRequest) =
        bulkOppslag({ ansattId }, { token.erCC }, brukerIds.map { BrukerIdOgRegelsett(it.verdi, regelType) }.toSet(),request.requestURI)

    private fun bulkOppslag(ansattId: () -> AnsattId, predikat: () -> Boolean, specs: Set<BrukerIdOgRegelsett>,uri: String) =
        with(ansattId()) {
            if (specs.isNotEmpty()) {
                MDC.put(USER_ID, ansattId().verdi)
                sjekk(predikat(), FORBIDDEN,"Mismatch mellom token type ${TokenType.from(token)} og $uri")
                sjekk(specs.size <= 1000, PAYLOAD_TOO_LARGE, "Maksimalt 1000 brukerId-er kan sendes i en bulk forespørsel")
                tell("bulk")
                regelTjeneste.bulkRegler( this, specs)
            }
            else {
                log.debug("Ingen brukerId-er oppgitt i bulk forespørsel for {}", this)
                AggregertBulkRespons(this)
            }
        }

    private fun enkeltOppslag(ansattId: () -> AnsattId, predikat: () -> Boolean, brukerId: String, regelType: RegelType, uri: String) =
        with(brukerId.trim('"')) {
            MDC.put(USER_ID, ansattId().verdi)
            log.trace("Kjører {} regler for {} og {}", regelType, ansattId(), this)
            if (!isProd) {
                if (brukerId.length == 11) {
                    MDC.put("brukerId", brukerId.take(10) + "X" + brukerId.takeLast(1))
                }
                else {
                    MDC.put("brukerId", brukerId)
                }
            }

            sjekk(predikat(), FORBIDDEN,"Mismatch mellom token type ${TokenType.from(token)} og $uri")
            sjekk(regelType in listOf(KJERNE_REGELTYPE,KOMPLETT_REGELTYPE),
                BAD_REQUEST, "Ugyldig regeltype: $regelType")
            tell("single")
            if (regelType == KJERNE_REGELTYPE) {
                return regelTjeneste.kjerneregler(ansattId(), this)
            }
            if (regelType == KOMPLETT_REGELTYPE) {
                return regelTjeneste.kompletteRegler(ansattId(), this)
            }
        }

    private fun tell(type: String) =
        teller.tell(Tags.of("type",type,"token",TokenType.from(token).name.lowercase()))


    private fun sjekk(predikat: Boolean, status: HttpStatus, message: String) {
        if (!predikat) throw ResponseStatusException(status,message)
    }
}
