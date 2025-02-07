package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangTjeneste
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import java.util.*

@SecurityScheme(
    bearerFormat = "JWT",
    name = "bearerAuth",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
)
@ProtectedRestController(value = ["/api/v1"], issuer = "azuread", claimMap = [])
class Tilgangskontroll(val service : TilgangTjeneste, val ansatt: EntraTjeneste, private val tokenUtil: TokenUtil) {

    @GetMapping("ansatt")
    @SecurityRequirement(name = "bearerAuth")
    fun hentAnsatt(ident: NavId) = ansatt.ansattAzureId(ident)

    @GetMapping("tilgang")
    @SecurityRequirement(name="bearerAuth")
    fun validerTilgang(kandidatIdent: Fødselsnummer) :Unit {
        val saksbehandlerNavIdent = tokenUtil.getNavIdentFromToken()
        return  service.sjekkTilgang(saksbehandlerNavIdent, kandidatIdent);

    }


}
@Component
class TokenUtil{
    val AAD_ISSUER: String = "aad"
    private var contextHolder: TokenValidationContextHolder? = null

    fun TokenUtil(contextHolder: TokenValidationContextHolder?) {
        this.contextHolder = contextHolder
    }
    fun getSubject(): String {
        return Optional.of(claimSet(AAD_ISSUER)).map { cs -> cs.getStringClaim("pid") }.orElse(null)
    }
    fun getIdentFromToken(): UUID {
        return Optional.of(claimSet(AAD_ISSUER)).map { cs -> UUID.fromString(cs.getStringClaim("oid")) }.orElse(null)
    }
    fun getNavIdentFromToken(): NavId {
        return  Optional.of(claimSet(AAD_ISSUER)).map { cs -> cs.getStringClaim("NAVident") as NavId }.orElse(null)
    }

    private fun claimSet(issuer: String): JwtTokenClaims {
        return Optional.ofNullable(contextHolder!!.getTokenValidationContext())
            .map { c -> c.getClaims(issuer) }
            .orElse(null)
    }
}

