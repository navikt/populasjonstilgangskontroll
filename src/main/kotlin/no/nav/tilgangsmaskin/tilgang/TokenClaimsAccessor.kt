package no.nav.tilgangsmaskin.tilgang

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenClaimsAccessor(
    private val contextHolder: TokenValidationContextHolder,
    private val env: ConfigurableEnvironment
) {


    val globaleGrupper
        get() = GlobalGruppe.entries.mapNotNull { gruppe ->
            val gruppeId = gruppe.id(env)
            claimSet()
                ?.getStringClaim(gruppeId.toString())
                ?.let { EntraGruppe(gruppeId, gruppe.name) }
        }.toSet()

    val system
        get() = runCatching {
            claimSet()?.getStringClaim("azp_name")
        }.getOrElse { "N/A" }
    val oidFraToken get() = claimSet()?.let { UUID.fromString(it.getStringClaim("oid")) }
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