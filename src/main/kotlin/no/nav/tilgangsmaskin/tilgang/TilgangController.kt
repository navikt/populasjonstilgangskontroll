package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.security.RequireCCF
import no.nav.tilgangsmaskin.felles.security.RequireOBO
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.tilgang.TilgangController.Companion.TILGANG_CONTROLLER_TAG_DESCRIPTION
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import no.nav.tilgangsmaskin.tilgang.openapi.ProblemDetailApiResponse
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus


@TilgangApiController
@ResponseStatus(NO_CONTENT)
@Tag(name = "TilgangController", description = TILGANG_CONTROLLER_TAG_DESCRIPTION)
class TilgangController(private val regelTjeneste: RegelTjeneste, token: Token) : TilgangControllerBase(token) {

    @PostMapping("komplett")
    @RequireOBO
    @ProblemDetailApiResponse(summary = SUMMARY_KOMPLETT_OBO, description = DESCRIPTION_KOMPLETT_OBO)
    fun kompletteRegler(@RequestBody brukerId: String) =
        enkeltOppslag(token.requiredAnsattId, brukerId, KOMPLETT_REGELTYPE)

    @PostMapping("/ccf/komplett/{ansattId}")
    @RequireCCF
    @ProblemDetailApiResponse(summary = SUMMARY_KOMPLETT_CCF, description = DESCRIPTION_KOMPLETT_CCF)
    fun kompletteReglerCCF(@PathVariable ansattId: AnsattId, @RequestBody brukerId: String) =
        enkeltOppslag(ansattId, brukerId, KOMPLETT_REGELTYPE)

    @PostMapping("kjerne")
    @RequireOBO
    @ProblemDetailApiResponse(summary = SUMMARY_KJERNE_OBO, description = DESCRIPTION_KJERNE_OBO)
    fun kjerneregler(@RequestBody brukerId: String) =
        enkeltOppslag(token.requiredAnsattId, brukerId, KJERNE_REGELTYPE)

    @PostMapping("/ccf/kjerne/{ansattId}")
    @RequireCCF
    @ProblemDetailApiResponse(summary = SUMMARY_KJERNE_CCF, description = DESCRIPTION_KJERNE_CCF)
    fun kjerneReglerCCF(@PathVariable ansattId: AnsattId, @RequestBody brukerId: String) =
        enkeltOppslag(ansattId, brukerId, KJERNE_REGELTYPE)

    private fun enkeltOppslag(ansatt: AnsattId, brukerId: String, regelType: RegelType) =
        with(brukerId.trim('"')) {
            sjekk(isNotBlank(), BAD_REQUEST, "brukerId kan ikke være tom")
            sjekk(regelType in listOf(KJERNE_REGELTYPE, KOMPLETT_REGELTYPE), BAD_REQUEST, "Ugyldig regeltype: $regelType")
            log.trace(CONFIDENTIAL, "Kjører {} regler for {} og {}", regelType, ansatt, maskFnr())
            when (regelType) {
                KJERNE_REGELTYPE -> regelTjeneste.kjerneregler(ansatt, this)
                else -> regelTjeneste.kompletteRegler(ansatt, this)
            }
        }

    private companion object {
         private const val TILGANG_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.tilgang.tag.description"
        private const val SUMMARY_KOMPLETT_OBO = "${MSG}openapi.tilgang.komplett.obo.summary"
        private const val DESCRIPTION_KOMPLETT_OBO = "${MSG}openapi.tilgang.komplett.obo.description"
        private const val SUMMARY_KOMPLETT_CCF = "${MSG}openapi.tilgang.komplett.ccf.summary"
        private const val DESCRIPTION_KOMPLETT_CCF = "${MSG}openapi.tilgang.komplett.ccf.description"
        private const val SUMMARY_KJERNE_OBO = "${MSG}openapi.tilgang.kjerne.obo.summary"
        private const val SUMMARY_KJERNE_CCF = "${MSG}openapi.tilgang.kjerne.ccf.summary"
        private const val DESCRIPTION_KJERNE_OBO = "${MSG}openapi.tilgang.kjerne.obo.description"
        private const val DESCRIPTION_KJERNE_CCF = "${MSG}openapi.tilgang.kjerne.ccf.description"
    }
}
