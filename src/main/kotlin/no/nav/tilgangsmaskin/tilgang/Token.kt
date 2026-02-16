package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.instrument.Tag
import no.nav.boot.conditionals.Cluster.LOCAL
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import java.util.*

@Component
class Token {

    private val jwt: Jwt?
        get() = SecurityContextHolder.getContext().authentication?.principal as? Jwt

    val globaleGruppeIds
        get() =
            jwt?.getClaimAsStringList("groups")
                ?.mapNotNull { UUID.fromString(it) }
                ?: emptyList()


    val system get() = stringClaim(AZP_NAME) ?: UTILGJENGELIG
    val oid get() = stringClaim(OID)?.let { UUID.fromString(it) }
    val ansattId get() = stringClaim(NAVIDENT)?.let { AnsattId(it) }
    private fun stringClaim(name: String) = jwt?.getClaimAsString(name)
    val clusterAndSystem get() = system.split(":").let { parts ->
        if (parts.size == 3) "${parts[2]}:${parts[0]}" else system
    }

    val systemNavn get() = system.split(":").lastOrNull() ?: UTILGJENGELIG
    val systemAndNs get() = runCatching { system.split(":").drop(1).joinToString(separator = ":") }.getOrElse { systemNavn }
    val cluster get() = runCatching { system.split(":").first() }.getOrElse { LOCAL.name.lowercase() }
    val erCC get() = stringClaim(IDTYP) == APP
    val erObo get()  = !erCC && oid != null
    companion object {
        private const val FLOW = "flow"
        const val AAD_ISSUER: String = "azuread"
        private const val APP = "app"
        private const val OID = "oid"
        private const val IDTYP = "idtyp"
        private const val AZP_NAME = "azp_name"
        private const val NAVIDENT = "NAVident"
        fun tokenTag(token: Token) = Tag.of(FLOW, TokenType.from(token).name.lowercase())

    }
}

enum class TokenType {
    OBO, CCF, UNAUTHENTICATED;

    companion object {
        fun from(token: Token): TokenType = when {
            token.erObo -> OBO
            token.erCC -> CCF
            else -> UNAUTHENTICATED
        }
    }
}