package no.nav.tilgangsmaskin.tilgang

import java.util.*
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.getIds
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class TokenClaimsAccessor(private val contextHolder: TokenValidationContextHolder) {

    private val log = getLogger(javaClass)


    fun globaleGrupper(): Set<EntraGruppe> {
        val claims = claimSet()?.getAsList("groups")
            ?.mapNotNull { it.toString().let(UUID::fromString) }
            ?: emptyList()
        return getIds().toMutableList().apply {
            retainAll(claims)
        }.toSet()
            .map { EntraGruppe(it, "") }.toSet()
        /*
        .also {
            log.info("Ansatt $ansattId er medlem av disse globale gruppene {}", it)
        }*/
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