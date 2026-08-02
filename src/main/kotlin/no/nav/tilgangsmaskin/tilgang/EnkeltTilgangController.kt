package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangData
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangGyldig
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangKonsumentValidator
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.tilgang.EnkeltTilgangController.Companion.ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION
import no.nav.tilgangsmaskin.tilgang.TokenType.OBO
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus


@TilgangApiController
@Tag(name = "EnkeltTilgangController", description = ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION)
class EnkeltTilgangController(private val enkelt: EnkeltTilgangTjeneste, private val validator: EnkeltTilgangKonsumentValidator, token: Token, teller: TokenTypeTeller) : TilgangControllerBase(token, teller) {

    @PostMapping("overstyr")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse(summary = SUMMARY_OVERSTYR, description = DESCRIPTION_OVERSTYR)
    fun enkeltTilgang(@RequestBody @Valid @EnkeltTilgangGyldig data: EnkeltTilgangData, req: HttpServletRequest) {
        sjekk(token.type == OBO, FORBIDDEN, "Forventet token type $OBO for ${req.requestURI}, fikk ${token.type}")
        validator.valider(token.systemNavn)
        enkelt.registrerTilgang(token.requiredAnsattId, data)
    }

    companion object {
        private const val ENKELTTILGANG_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.tilgang.tag.description"
        private const val SUMMARY_OVERSTYR = "${MSG}openapi.tilgang.overstyr.summary"
        private const val DESCRIPTION_OVERSTYR = "${MSG}openapi.tilgang.overstyr.description"
    }
}
