package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.DESCRIPTION_CACHE_FLUSH
import no.nav.tilgangsmaskin.felles.cache.SUMMARY_CACHE_FLUSH
import no.nav.tilgangsmaskin.felles.rest.ProdController
import no.nav.tilgangsmaskin.felles.security.OAuth2RequireCCF
import no.nav.tilgangsmaskin.felles.security.OOAuth2RequireOBO
import no.nav.tilgangsmaskin.felles.security.requiredAnsattId
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import no.nav.tilgangsmaskin.tilgang.openapi.ProblemDetailApiResponse
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

private const val TILGANG_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.tilgang.tag.description"
private const val SUMMARY_KOMPLETT_OBO = "${MSG}openapi.tilgang.komplett.obo.summary"
private const val DESCRIPTION_KOMPLETT_OBO = "${MSG}openapi.tilgang.komplett.obo.description"
private const val SUMMARY_KOMPLETT_CCF = "${MSG}openapi.tilgang.komplett.ccf.summary"
private const val DESCRIPTION_KOMPLETT_CCF = "${MSG}openapi.tilgang.komplett.ccf.description"
private const val SUMMARY_KJERNE_OBO = "${MSG}openapi.tilgang.kjerne.obo.summary"
private const val SUMMARY_KJERNE_CCF = "${MSG}openapi.tilgang.kjerne.ccf.summary"
private const val DESCRIPTION_KJERNE_OBO = "${MSG}openapi.tilgang.kjerne.obo.description"
private const val DESCRIPTION_KJERNE_CCF = "${MSG}openapi.tilgang.kjerne.ccf.description"


@ProdController
@ResponseStatus(NO_CONTENT)
@Tag(name = "TilgangController", description = TILGANG_CONTROLLER_TAG_DESCRIPTION)
class TilgangController(private val regelTjeneste: RegelTjeneste, private val cache: CacheOperations) {

    private val log = getLogger(javaClass)

    @PostMapping("komplett")
    @OOAuth2RequireOBO
    @ProblemDetailApiResponse(summary = SUMMARY_KOMPLETT_OBO, description = DESCRIPTION_KOMPLETT_OBO)
    fun kompletteRegler(@AuthenticationPrincipal principal: OAuth2AuthenticatedPrincipal, @RequestBody brukerId: String) =
        enkeltOppslag(principal.requiredAnsattId(), brukerId, KOMPLETT_REGELTYPE)

    @PostMapping("/ccf/komplett/{ansattId}")
    @OAuth2RequireCCF
    @ProblemDetailApiResponse(summary = SUMMARY_KOMPLETT_CCF, description = DESCRIPTION_KOMPLETT_CCF)
    fun kompletteReglerCCF(@PathVariable ansattId: AnsattId, @RequestBody brukerId: String) =
        enkeltOppslag(ansattId, brukerId, KOMPLETT_REGELTYPE)

    @PostMapping("kjerne")
    @OOAuth2RequireOBO
    @ProblemDetailApiResponse(summary = SUMMARY_KJERNE_OBO, description = DESCRIPTION_KJERNE_OBO)
    fun kjerneregler(@AuthenticationPrincipal principal: OAuth2AuthenticatedPrincipal, @RequestBody brukerId: String) =
        enkeltOppslag(principal.requiredAnsattId(), brukerId, KJERNE_REGELTYPE)

    @PostMapping("/ccf/kjerne/{ansattId}")
    @OAuth2RequireCCF
    @ProblemDetailApiResponse(summary = SUMMARY_KJERNE_CCF, description = DESCRIPTION_KJERNE_CCF)
    fun kjerneReglerCCF(@PathVariable ansattId: AnsattId, @RequestBody brukerId: String) =
        enkeltOppslag(ansattId, brukerId, KJERNE_REGELTYPE)

    @OOAuth2RequireOBO
    @DeleteMapping("cache/flush")
    @Operation(summary = SUMMARY_CACHE_FLUSH, description = DESCRIPTION_CACHE_FLUSH)
    fun flushId() = true
        /*
        with(jwt.requiredAnsattId().verdi) {
            cache.delete(OID_CACHE,this).also {
                if (it) log.info("Slettet cache innslag i cache ${OID_CACHE.fullName} for $this")
                else log.trace("Fant ikke cache innslag i cache ${OID_CACHE.fullName} for $this")
            }
        } */


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
}
