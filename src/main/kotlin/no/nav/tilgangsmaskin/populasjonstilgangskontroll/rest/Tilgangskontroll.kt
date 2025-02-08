package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TilgangTjeneste
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import java.util.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest.TokenUtil.Companion.AAD_ISSUER
import org.slf4j.LoggerFactory.getLogger

@SecurityScheme(
    bearerFormat = "JWT",
    name = "bearerAuth",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
)
@ProtectedRestController(value = ["/api/v1"], issuer = AAD_ISSUER, claimMap = [])
class Tilgangskontroll(val service : TilgangTjeneste, val ansatt: EntraTjeneste, private val tokenUtil: TokenUtil) {

    private val log = getLogger(Tilgangskontroll::class.java)

    @GetMapping("ansatt")
    @SecurityRequirement(name = "bearerAuth")
    fun hentAnsatt(ident: NavId) = ansatt.ansattAzureId(ident)

    @GetMapping("tilgang")
    @SecurityRequirement(name="bearerAuth")
    // TODO Gjør om til POST
    fun validerTilgang(kandidatIdent: Fødselsnummer) {
        tokenUtil.all.forEach { (k,v) -> log.info("$k->$v") }
        val saksbehandlerNavIdent = tokenUtil.navIdentFromToken
        service.sjekkTilgang(saksbehandlerNavIdent, kandidatIdent);
    }


}
@Component
// TODO bedre feilhåndtering, bruk konstanter for oid  og pid
class TokenUtil(private val contextHolder: TokenValidationContextHolder){

    val all get() = claimSet().allClaims
    val subject get()  = claimSet().getStringClaim("pid")
    val  identFromToken get()  = claimSet().let { UUID.fromString(it.getStringClaim("oid")) }
    val navIdentFromToken get()  = claimSet().getStringClaim("NAVident")?.let { NavId(it) } ?: throw RuntimeException("NAVident claim not found in token")
    private fun claimSet() = contextHolder.getTokenValidationContext().getClaims(AAD_ISSUER)

    companion object {

        const val AAD_ISSUER: String = "azuread"
    }
}

