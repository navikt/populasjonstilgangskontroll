package no.nav.tilgangsmaskin.populasjonstilgangskontroll.tilgang1

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenClaimsAccessor (private val contextHolder: TokenValidationContextHolder){

    val system get() = runCatching { claimSet()?.getStringClaim("azp_name")
    }.getOrElse { "N/A" }
    val  oidFraToken get()  = claimSet()?.let { UUID.fromString(it.getStringClaim("oid")) }
    val ansattId get()  = claimSet()?.getStringClaim("NAVident")?.let { AnsattId(it) }
    private fun claimSet() = runCatching {
        contextHolder.getTokenValidationContext().getClaims(AAD_ISSUER)
    }.getOrNull()

    val systemNavn get() = system?.split(":")?.lastOrNull() ?: "N/A"

    val systemAndNamespace get() = runCatching {
        system?.split(":")?.drop(1)?.joinToString (separator = ":") ?: systemNavn
    }.getOrElse { systemNavn }

    companion object {

        const val AAD_ISSUER: String = "azuread"
    }
}