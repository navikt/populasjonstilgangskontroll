package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.instrument.Tags
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.TokenTypeTeller
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangData
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangGyldig
import no.nav.tilgangsmaskin.tilgang.Token.Companion.AAD_ISSUER
import no.nav.tilgangsmaskin.tilgang.TokenType.CCF
import no.nav.tilgangsmaskin.tilgang.TokenType.OBO
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.MULTI_STATUS
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

private const val TILGANG_CONTROLLER_TAG_DESCRIPTION = "msg:openapi.tilgang.tag.description"

@SecurityScheme(bearerFormat = "JWT", name = "bearerAuth", scheme = "bearer", type = HTTP)
@ProtectedRestController(value = ["/api/v1"], issuer = AAD_ISSUER, claimMap = [])
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "TilgangController", description = TILGANG_CONTROLLER_TAG_DESCRIPTION)
class TilgangController(
    private val regelTjeneste: RegelTjeneste,
    private val enkeltTilgangTjeneste: EnkeltTilgangTjeneste,
    private val token: Token,
    private val guard: TokenTypeGuard,
    private val teller: TokenTypeTeller) {

    private val log = getLogger(javaClass)

    @PostMapping("komplett")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_KOMPLETT_OBO, description = DESCRIPTION_KOMPLETT_OBO)
    fun kompletteRegler(@RequestBody brukerId: String, req: HttpServletRequest) =
        enkeltOppslag({ ansattIdFraToken() }, OBO, brukerId, KOMPLETT_REGELTYPE,req.requestURI)

    @PostMapping("/ccf/komplett/{ansattId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_KOMPLETT_CCF, description = DESCRIPTION_KOMPLETT_CCF)
    fun kompletteReglerCCF(@PathVariable ansattId: AnsattId, @RequestBody brukerId: String, req: HttpServletRequest) =
        enkeltOppslag({ansattId}, CCF, brukerId, KOMPLETT_REGELTYPE, req.requestURI)

    @PostMapping("kjerne")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_KJERNE_OBO, description = DESCRIPTION_KJERNE_OBO)
    fun kjerneregler(@RequestBody brukerId: String, req: HttpServletRequest) =
        enkeltOppslag({ ansattIdFraToken() }, OBO, brukerId, KJERNE_REGELTYPE, req.requestURI)


    @PostMapping("/ccf/kjerne/{ansattId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_KJERNE_CCF, description = DESCRIPTION_KJERNE_CCF)
    fun kjerneReglerCCF(@PathVariable ansattId: AnsattId, @RequestBody brukerId: String, req: HttpServletRequest) =
        enkeltOppslag({ansattId}, CCF, brukerId, KJERNE_REGELTYPE,req.requestURI)


    @PostMapping("overstyr")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_OVERSTYR, description = DESCRIPTION_OVERSTYR)
    fun overstyr(@RequestBody @Valid @EnkeltTilgangGyldig data: EnkeltTilgangData, req: HttpServletRequest) {
        guard.krev(OBO, req.requestURI)
        enkeltTilgangTjeneste.registrerEnkeltTilgang(ansattIdFraToken(), data, token.systemNavn)
    }

    @PostMapping("bulk/obo")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    @Operation(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_OBO)
    fun bulkOBO(@RequestBody  specs: Set<BrukerIdOgRegelsett>, req: HttpServletRequest) =
        bulkOppslag({ ansattIdFraToken() }, OBO, specs,req.requestURI)

    @PostMapping("bulk/obo/{regelType}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    @Operation(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_OBO_REGELTYPE)
    fun bulkOBOForRegelType(@PathVariable regelType: RegelType, @RequestBody brukerIds: Set<String>, req: HttpServletRequest) =
        bulkOppslag({ ansattIdFraToken() },
            OBO, brukerIds.map { BrukerIdOgRegelsett(it,regelType) }.toSet(),req.requestURI)

    @PostMapping("bulk/ccf/{ansattId}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    @Operation(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_CCF)
    fun bulkCCF(@PathVariable ansattId: AnsattId, @RequestBody specs: Set<BrukerIdOgRegelsett>, req: HttpServletRequest) =
        bulkOppslag({ansattId}, CCF, specs,req.requestURI)

    @PostMapping("bulk/ccf/{ansattId}/{regelType}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    @Operation(summary = SUMMARY_BULK, description = DESCRIPTION_BULK_CCF_REGELTYPE)
    fun bulkCCFForRegelType(@PathVariable ansattId: AnsattId, @PathVariable regelType: RegelType, @RequestBody brukerIds: Set<String>, req: HttpServletRequest) =
        bulkOppslag({ ansattId }, CCF, brukerIds.map { BrukerIdOgRegelsett(it, regelType) }.toSet(),req.requestURI)

    private fun bulkOppslag(ansattId: () -> AnsattId, forventet: TokenType, specs: Set<BrukerIdOgRegelsett>, uri: String): AggregertBulkRespons {
        guard.krev(forventet, uri)
        val ansatt = ansattId()
        MDC.put(USER_ID, ansatt.verdi)
        return if (specs.isNotEmpty()) {
            sjekk(specs.size <= 1000, PAYLOAD_TOO_LARGE, "Maksimalt 1000 brukerId-er kan sendes i en bulk forespørsel")
            sjekk(specs.none { it.brukerId.isBlank() }, BAD_REQUEST, "brukerId kan ikke være tom")
            tell("bulk")
            regelTjeneste.bulkRegler(ansatt, specs)
        } else {
            log.debug("Ingen brukerId-er oppgitt i bulk forespørsel for {}", ansatt)
            AggregertBulkRespons(ansatt)
        }
    }

    private fun enkeltOppslag(ansattId: () -> AnsattId, forventet: TokenType, brukerId: String, regelType: RegelType, uri: String) =
        with(brukerId.trim('"')) {
            sjekk(isNotBlank(), BAD_REQUEST, "brukerId kan ikke være tom")
            guard.krev(forventet, uri)
            val ansatt = ansattId()
            MDC.put(USER_ID, ansatt.verdi)
            log.trace(CONFIDENTIAL,"Kjører {} regler for {} og {}", regelType, ansatt, this.maskFnr())
            sjekk(regelType in listOf(KJERNE_REGELTYPE,KOMPLETT_REGELTYPE),
                BAD_REQUEST, "Ugyldig regeltype: $regelType")
            tell("single")
            when (regelType) {
                KJERNE_REGELTYPE -> regelTjeneste.kjerneregler(ansatt, this)
                else -> regelTjeneste.kompletteRegler(ansatt, this)
            }
        }

    private fun tell(type: String) =
        teller.tell(Tags.of("type",type,"token",TokenType.from(token).name.lowercase()))


    private fun sjekk(predikat: Boolean, status: HttpStatus, message: String) {
        if (!predikat) throw ResponseStatusException(status,message)
    }

    private fun ansattIdFraToken(): AnsattId =
        requireNotNull(token.ansattId) { "Mangler ansattId i OBO-token" }

    companion object {
        private const val SUMMARY_KOMPLETT_OBO =
            "msg:openapi.tilgang.komplett.obo.summary"
        private const val DESCRIPTION_KOMPLETT_OBO =
            "msg:openapi.tilgang.komplett.obo.description"
        private const val SUMMARY_KOMPLETT_CCF =
            "msg:openapi.tilgang.komplett.ccf.summary"
        private const val DESCRIPTION_KOMPLETT_CCF =
            "msg:openapi.tilgang.komplett.ccf.description"
        private const val SUMMARY_KJERNE_OBO =
            "msg:openapi.tilgang.kjerne.obo.summary"
        private const val SUMMARY_KJERNE_CCF =
            "msg:openapi.tilgang.kjerne.ccf.summary"
        private const val DESCRIPTION_KJERNE_OBO =
            "msg:openapi.tilgang.kjerne.obo.description"
        private const val DESCRIPTION_KJERNE_CCF =
            "msg:openapi.tilgang.kjerne.ccf.description"
        private const val SUMMARY_OVERSTYR =
            "msg:openapi.tilgang.overstyr.summary"
        private const val DESCRIPTION_OVERSTYR =
            "msg:openapi.tilgang.overstyr.description"
        private const val SUMMARY_BULK = "msg:openapi.tilgang.bulk.summary"
        private const val DESCRIPTION_BULK_OBO =
            "msg:openapi.tilgang.bulk.obo.description"
        private const val DESCRIPTION_BULK_OBO_REGELTYPE = "msg:openapi.tilgang.bulk.obo.regeltype.description"
        private const val DESCRIPTION_BULK_CCF = "msg:openapi.tilgang.bulk.ccf.description"
        private const val DESCRIPTION_BULK_CCF_REGELTYPE = "msg:openapi.tilgang.bulk.ccf.regeltype.description"
    }
}
