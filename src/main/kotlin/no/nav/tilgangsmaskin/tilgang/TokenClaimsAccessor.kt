package no.nav.tilgangsmaskin.tilgang

import java.util.*
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class TokenClaimsAccessor(private val contextHolder: TokenValidationContextHolder) {

    private val log = getLogger(javaClass)


    val globaleGrupper
        get() = {
            val claims = claimSet()?.getAsList("groups")
                ?.mapNotNull { it.toString().let(UUID::fromString) }
                ?: emptyList()
            GlobalGruppe.getIds().toMutableList().apply {
                retainAll(claims)
            }
        }

    val system
        get() = runCatching {
            claimSet()?.getStringClaim("azp_name")
        }.getOrElse { "N/A" }
    val oid get() = claimSet()?.let { UUID.fromString(it.getStringClaim("oid")) }
    val ansattId get() = claimSet()?.getStringClaim("NAVident")?.let { AnsattId(it) }
    private fun claimSet() = runCatching {
        contextHolder.getTokenValidationContext().getClaims(AAD_ISSUER)
    }.getOrNull()

    val systemNavn get() = system?.split(":")?.lastOrNull() ?: "N/A"

    val systemAndNamespace
        get() = runCatching {
            system?.split(":")?.drop(1)?.joinToString(separator = ":") ?: systemNavn
        }.getOrElse { systemNavn }

    companion object {
        const val AAD_ISSUER: String = "azuread"
    }
}