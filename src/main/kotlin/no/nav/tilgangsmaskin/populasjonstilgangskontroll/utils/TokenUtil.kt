package no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import org.springframework.stereotype.Component
import java.util.UUID

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