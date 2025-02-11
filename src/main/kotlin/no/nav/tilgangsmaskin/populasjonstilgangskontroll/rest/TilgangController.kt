package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenAccessor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenAccessor.Companion.AAD_ISSUER
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@SecurityScheme(bearerFormat = "JWT", name = "bearerAuth", scheme = "bearer", type = HTTP )
@ProtectedRestController(value = ["/api/v1"], issuer = AAD_ISSUER, claimMap = [])
@SecurityRequirement(name = "bearerAuth")
class TilgangController(val regler : RegelTjeneste, val ansatt: EntraTjeneste, private val token: TokenAccessor) {


    @GetMapping("ansatt")
    fun hentAnsatt(navId: NavId) = ansatt.ansattAzureId(navId)

    @PostMapping("tilgang")
    fun validerTilgang(@RequestBody kandidatId: Fødselsnummer) = regler.sjekkTilgang(token.navIdent, kandidatId);

}

