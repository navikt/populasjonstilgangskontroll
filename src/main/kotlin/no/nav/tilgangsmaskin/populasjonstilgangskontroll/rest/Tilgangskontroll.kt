package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangTjeneste
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping

@SecurityScheme(
    bearerFormat = "JWT",
    name = "bearerAuth",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
)
@ProtectedRestController(value = ["/api/v1"], issuer = "azuread", claimMap = [])
class Tilgangskontroll(val service : TilgangTjeneste, val ansatt: EntraTjeneste,) {

    @GetMapping("ansatt")
    @SecurityRequirement(name = "bearerAuth")
    fun hentAnsatt(ident: NavId) = ansatt.ansattAzureId(ident)
}

