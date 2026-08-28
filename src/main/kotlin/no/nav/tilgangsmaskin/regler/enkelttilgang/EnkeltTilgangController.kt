package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.tilgangsmaskin.felles.security.OAuth2RequireOBO
import no.nav.tilgangsmaskin.felles.rest.ProdController
import no.nav.tilgangsmaskin.felles.security.ansattId
import no.nav.tilgangsmaskin.regler.enkelttilgang.openapi.ValideringsfeilApiResponse
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

private const val ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.tilgang.tag.description"
 const val SUMMARY_OVERSTYR = "${MSG}openapi.tilgang.overstyr.summary"
 const val DESCRIPTION_OVERSTYR = "${MSG}openapi.tilgang.overstyr.description"


@ProdController
@OAuth2RequireOBO
@Tag(name = "EnkeltTilgangController", description = ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION)
class EnkeltTilgangController(private val enkelt: EnkeltTilgangTjeneste) {

    @PostMapping("overstyr")
    @ResponseStatus(NO_CONTENT)
    @ValideringsfeilApiResponse
    fun enkeltTilgang(@AuthenticationPrincipal principal: OAuth2AuthenticatedPrincipal, @RequestBody @EnkeltTilgangGyldig data: EnkeltTilgangData) {
        enkelt.registrerTilgang(principal.ansattId(), data)
    }
}
