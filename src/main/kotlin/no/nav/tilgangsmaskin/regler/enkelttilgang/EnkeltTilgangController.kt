package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.security.OOAuth2RequireOBO
import no.nav.tilgangsmaskin.felles.rest.ProdController
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import no.nav.tilgangsmaskin.tilgang.openapi.ProblemDetailApiResponse
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

private const val ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.tilgang.tag.description"
private const val SUMMARY_OVERSTYR = "${MSG}openapi.tilgang.overstyr.summary"
private const val DESCRIPTION_OVERSTYR = "${MSG}openapi.tilgang.overstyr.description"


@ProdController
@OOAuth2RequireOBO
@Tag(name = "EnkeltTilgangController", description = ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION)
class EnkeltTilgangController(private val enkelt: EnkeltTilgangTjeneste, private val token: Token) {

    @PostMapping("overstyr")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse(summary = SUMMARY_OVERSTYR, description = DESCRIPTION_OVERSTYR)
    fun enkeltTilgang(@RequestBody @Valid @EnkeltTilgangGyldig data: EnkeltTilgangData) {
        enkelt.registrerTilgang(token.requiredAnsattId, data)
    }
}
