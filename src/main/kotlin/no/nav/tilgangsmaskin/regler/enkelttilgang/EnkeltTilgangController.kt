package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.rest.TokenType.OBO
import no.nav.tilgangsmaskin.felles.rest.TokenTypeTeller
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangController.Companion.ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION
import no.nav.tilgangsmaskin.tilgang.TilgangApiController
import no.nav.tilgangsmaskin.tilgang.TilgangControllerBase
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import no.nav.tilgangsmaskin.tilgang.openapi.ProblemDetailApiResponse
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus


@TilgangApiController
@Tag(name = "EnkeltTilgangController", description = ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION)
class EnkeltTilgangController(private val enkelt: EnkeltTilgangTjeneste, token: Token, teller: TokenTypeTeller) : TilgangControllerBase(token, teller) {

    @PostMapping("overstyr")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse(summary = SUMMARY_OVERSTYR, description = DESCRIPTION_OVERSTYR)
    fun enkeltTilgang(@RequestBody @Valid @EnkeltTilgangGyldig data: EnkeltTilgangData) {
        sjekk(token.type == OBO, FORBIDDEN, "Forventet token type $OBO for overstyr, fikk ${token.type}")
        enkelt.registrerTilgang(token.requiredAnsattId, data)
    }

    companion object {
        private const val ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.tilgang.tag.description"
        private const val SUMMARY_OVERSTYR = "${MSG}openapi.tilgang.overstyr.summary"
        private const val DESCRIPTION_OVERSTYR = "${MSG}openapi.tilgang.overstyr.description"
    }
}
