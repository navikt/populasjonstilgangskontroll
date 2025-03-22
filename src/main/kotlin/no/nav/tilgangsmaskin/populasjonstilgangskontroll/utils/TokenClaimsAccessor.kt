package no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenClaimsAccessor(private val contextHolder: TokenValidationContextHolder){

    val system get() = runCatching {
        claimSet().getStringClaim("azp_name")
    }.getOrElse { "N/A" }
    val  oidFraToken get()  = claimSet().let { UUID.fromString(it.getStringClaim("oid")) }
    val ansattId get()  = claimSet().getStringClaim("NAVident")?.let { AnsattId(it) } ?: throw RuntimeException("NAVident claim not found in token")
    private fun claimSet() = contextHolder.getTokenValidationContext().getClaims(AAD_ISSUER)

    companion object {

        const val AAD_ISSUER: String = "azuread"
    }
}