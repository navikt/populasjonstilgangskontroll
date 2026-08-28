package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import no.nav.tilgangsmaskin.felles.security.OAuth2RequireOBO
import no.nav.tilgangsmaskin.felles.rest.ProdController
import no.nav.tilgangsmaskin.felles.security.ansattId
import no.nav.tilgangsmaskin.regler.enkelttilgang.openapi.ValideringsfeilApiResponse
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import no.nav.tilgangsmaskin.tilgang.openapi.ProblemDetailApiResponse
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

private const val ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.tilgang.tag.description"
private const val SUMMARY_OVERSTYR = "${MSG}openapi.tilgang.overstyr.summary"
private const val DESCRIPTION_OVERSTYR = "${MSG}openapi.tilgang.overstyr.description"


@ProdController
@OAuth2RequireOBO
@Tag(name = "EnkeltTilgangController", description = ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION)
class EnkeltTilgangController(private val enkelt: EnkeltTilgangTjeneste) {

    @PostMapping("overstyr")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse(summary = SUMMARY_OVERSTYR, description = DESCRIPTION_OVERSTYR)
    @ValideringsfeilApiResponse
    fun enkeltTilgang(@AuthenticationPrincipal principal: OAuth2AuthenticatedPrincipal, @RequestBody @EnkeltTilgangGyldig data: EnkeltTilgangData) {
        enkelt.registrerTilgang(principal.ansattId(), data)
    }
}
