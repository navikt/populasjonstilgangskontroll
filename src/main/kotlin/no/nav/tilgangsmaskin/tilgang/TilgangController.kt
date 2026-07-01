package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.tilgang.TokenType.CCF
import no.nav.tilgangsmaskin.tilgang.TokenType.OBO
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

private const val TILGANG_CONTROLLER_TAG_DESCRIPTION = "msg:openapi.tilgang.tag.description"

@TilgangApiController
@ResponseStatus(NO_CONTENT)
@Tag(name = "TilgangController", description = TILGANG_CONTROLLER_TAG_DESCRIPTION)
class TilgangController(
    private val regelTjeneste: RegelTjeneste, guard: TokenTypeGuard,
    teller: TokenTypeTeller) : TilgangControllerBase(guard, teller) {


    @PostMapping("komplett")
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_KOMPLETT_OBO, description = DESCRIPTION_KOMPLETT_OBO)
    fun kompletteRegler(@RequestBody brukerId: String, req: HttpServletRequest) =
        enkeltOppslag({ ansattIdFraToken() }, OBO, brukerId, KOMPLETT_REGELTYPE, req.requestURI)

    @PostMapping("/ccf/komplett/{ansattId}")
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_KOMPLETT_CCF, description = DESCRIPTION_KOMPLETT_CCF)
    fun kompletteReglerCCF(@PathVariable ansattId: AnsattId, @RequestBody brukerId: String, req: HttpServletRequest) =
        enkeltOppslag({ ansattId }, CCF, brukerId, KOMPLETT_REGELTYPE, req.requestURI)

    @PostMapping("kjerne")
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_KJERNE_OBO, description = DESCRIPTION_KJERNE_OBO)
    fun kjerneregler(@RequestBody brukerId: String, req: HttpServletRequest) =
        enkeltOppslag({ ansattIdFraToken() }, OBO, brukerId, KJERNE_REGELTYPE, req.requestURI)

    @PostMapping("/ccf/kjerne/{ansattId}")
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_KJERNE_CCF, description = DESCRIPTION_KJERNE_CCF)
    fun kjerneReglerCCF(@PathVariable ansattId: AnsattId, @RequestBody brukerId: String, req: HttpServletRequest) =
        enkeltOppslag({ ansattId }, CCF, brukerId, KJERNE_REGELTYPE, req.requestURI)

    private fun enkeltOppslag(
        ansattId: () -> AnsattId,
        forventet: TokenType,
        brukerId: String,
        regelType: RegelType,
        uri: String,
    ) =
        with(brukerId.trim('"')) {
            sjekk(isNotBlank(), BAD_REQUEST, "brukerId kan ikke være tom")
            guard.krev(forventet, uri)
            val ansatt = ansattId()
            logSingle(regelType, ansatt, this)
            sjekk(
                regelType in listOf(KJERNE_REGELTYPE, KOMPLETT_REGELTYPE),
                BAD_REQUEST,
                "Ugyldig regeltype: $regelType",
            )
            tell("single", forventet)
            when (regelType) {
                KJERNE_REGELTYPE -> regelTjeneste.kjerneregler(ansatt, this)
                else -> regelTjeneste.kompletteRegler(ansatt, this)
            }
        }

    private fun logSingle(regelType: RegelType, ansatt: AnsattId, brukerId: String) =
        log.trace(CONFIDENTIAL, "Kjører {} regler for {} og {}", regelType, ansatt, brukerId.maskFnr())

    companion object {
        private const val SUMMARY_KOMPLETT_OBO = "msg:openapi.tilgang.komplett.obo.summary"
        private const val DESCRIPTION_KOMPLETT_OBO = "msg:openapi.tilgang.komplett.obo.description"
        private const val SUMMARY_KOMPLETT_CCF = "msg:openapi.tilgang.komplett.ccf.summary"
        private const val DESCRIPTION_KOMPLETT_CCF = "msg:openapi.tilgang.komplett.ccf.description"
        private const val SUMMARY_KJERNE_OBO = "msg:openapi.tilgang.kjerne.obo.summary"
        private const val SUMMARY_KJERNE_CCF = "msg:openapi.tilgang.kjerne.ccf.summary"
        private const val DESCRIPTION_KJERNE_OBO = "msg:openapi.tilgang.kjerne.obo.description"
        private const val DESCRIPTION_KJERNE_CCF = "msg:openapi.tilgang.kjerne.ccf.description"
    }
}
