package no.nav.tilgangsmaskin.tilgang

import java.util.*
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.tilgangsmaskin.ansatt.AnsattId
import org.springframework.stereotype.Component

@Component
class Token(private val contextHolder: TokenValidationContextHolder) {


    val globaleGruppeIds
        get() =
            claimSet()?.getAsList("groups")
                ?.mapNotNull { UUID.fromString(it) }
                ?: emptyList()


    val system get() = runCatching { claimSet()?.getStringClaim("azp_name") }.getOrElse { "N/A" } ?: "N/A"
    val oid get() = claimSet()?.let { UUID.fromString(it.getStringClaim("oid")) }
    val ansattId get() = claimSet()?.getStringClaim("NAVident")?.let { AnsattId(it) }
    private fun claimSet() = runCatching { contextHolder.getTokenValidationContext().getClaims(AAD_ISSUER) }.getOrNull()
    val systemNavn get() = system.split(":").lastOrNull() ?: "N/A"
    val systemAndNs get() = runCatching { system.split(":").drop(1).joinToString(separator = ":") }.getOrElse { systemNavn }
    val erCC get() =  claimSet()?.getStringClaim(IDTYP)?.let { it == APP } ?: false
    val erObo get() =  !erCC
    companion object {
        const val AAD_ISSUER: String = "azuread"
        private const val APP = "app"
        private const val IDTYP = "idtyp"
    }
}