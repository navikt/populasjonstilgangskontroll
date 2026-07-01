package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangData
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangGyldig
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangKonsumentValidator
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.tilgang.TokenType.OBO
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

private const val OVERSTYR_TILGANG_CONTROLLER_TAG_DESCRIPTION = "msg:openapi.tilgang.tag.description"

@TilgangApiController
@Tag(name = "EnkeltTilgangController", description = OVERSTYR_TILGANG_CONTROLLER_TAG_DESCRIPTION)
class EnkeltTilgangController(
    private val enkeltTilgangTjeneste: EnkeltTilgangTjeneste,
    private val konsumentValidator: EnkeltTilgangKonsumentValidator,
    token: Token,
    teller: TokenTypeTeller,
) : TilgangControllerBase(token, teller) {

    @PostMapping("overstyr")
    @ResponseStatus(ACCEPTED)
    @ProblemDetailApiResponse
    @Operation(summary = SUMMARY_OVERSTYR, description = DESCRIPTION_OVERSTYR)
    fun enkeltTilgang(@RequestBody @Valid @EnkeltTilgangGyldig data: EnkeltTilgangData, req: HttpServletRequest) {
        sjekk(token.type == OBO, FORBIDDEN, "Forventet token type $OBO for ${req.requestURI}, fikk ${token.type}")
        konsumentValidator.valider(token.systemNavn)
        enkeltTilgangTjeneste.registrerEnkeltTilgang(ansattIdFraToken(), data)
    }

    companion object {
        private const val SUMMARY_OVERSTYR = "msg:openapi.tilgang.overstyr.summary"
        private const val DESCRIPTION_OVERSTYR = "msg:openapi.tilgang.overstyr.description"
    }
}
