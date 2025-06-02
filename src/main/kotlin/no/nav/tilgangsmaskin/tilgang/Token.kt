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


    val system get() = stringClaim(AZP_NAME)  ?: "N/A"
    val oid get() = stringClaim(OID)?.let { UUID.fromString(it) }
    val ansattId get() = stringClaim(NAVIDENT)?.let { AnsattId(it) }
    private val idType get() = stringClaim(IDTYP)
    private fun stringClaim(name: String) = claimSet()?.getStringClaim(name)
    private fun claimSet() = runCatching { contextHolder.getTokenValidationContext().getClaims(AAD_ISSUER) }.getOrNull()
    val systemNavn get() = system.split(":").lastOrNull() ?: "N/A"
    val systemAndNs get() = runCatching { system.split(":").drop(1).joinToString(separator = ":") }.getOrElse { systemNavn }
    val erCC get() = idType == APP
    val erObo get()  = oid != null
    companion object {
        const val AAD_ISSUER: String = "azuread"
        private const val APP = "app"
        private const val OID = "oid"
        private const val IDTYP = "idtyp"
        private const val AZP_NAME = "azp_name"
        private const val NAVIDENT = "NAVident"


    }
}