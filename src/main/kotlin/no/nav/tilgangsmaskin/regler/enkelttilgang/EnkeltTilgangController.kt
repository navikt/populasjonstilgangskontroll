package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.tilgangsmaskin.felles.security.RequireOAuth2OBOAndEnkelt
import no.nav.tilgangsmaskin.felles.rest.ProdController
import no.nav.tilgangsmaskin.felles.security.ansattId
import no.nav.tilgangsmaskin.regler.enkelttilgang.openapi.EnkeltTilgangApiResponse
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.HandlerMethodValidationException

private const val ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.tilgang.tag.description"
 const val SUMMARY_ENKELTTILGANG = "${MSG}openapi.tilgang.enkelttilgang.summary"
 const val DESCRIPTION_ENKELTTILGANG = "${MSG}openapi.tilgang.enkelttilgang.description"


@ProdController
@RequireOAuth2OBOAndEnkelt
@Tag(name = "EnkeltTilgangController", description = ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION)
class EnkeltTilgangController(private val enkelt: EnkeltTilgangTjeneste) {

    @PostMapping("overstyr")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = SUMMARY_ENKELTTILGANG, description = DESCRIPTION_ENKELTTILGANG)
    @EnkeltTilgangApiResponse
    fun enkeltTilgang(@AuthenticationPrincipal principal: OAuth2AuthenticatedPrincipal, @RequestBody @EnkeltTilgangGyldig data: EnkeltTilgangData) {
        enkelt.registrerTilgang(principal.ansattId(), data)
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleValidation(ex: HandlerMethodValidationException) = valideringsfeilRespons(ex)
}
