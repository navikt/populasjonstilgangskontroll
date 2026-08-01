package no.nav.tilgangsmaskin.tilgang

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.tilgang.TokenType.CCF
import no.nav.tilgangsmaskin.tilgang.TokenType.OBO
import no.nav.tilgangsmaskin.tilgang.TokenType.UNAUTHENTICATED
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import java.util.*

@Component
class Token {


    val globaleGruppeIds
        get() =
            jwtToken()?.token?.getClaimAsStringList(GROUPS)
                ?.mapNotNullTo(mutableSetOf()) { runCatching { UUID.fromString(it) }.getOrNull() }
                .orEmpty()


    val system get() = stringClaim(AZP_NAME) ?: UTILGJENGELIG
    val oid get() = stringClaim(OID)?.let { runCatching { UUID.fromString(it) }.getOrNull() }
    val ansattId get() = stringClaim(NAVIDENT)?.let { AnsattId(it) }
    private fun stringClaim(name: String) = jwtToken()?.token?.getClaimAsString(name)
    private fun jwtToken() = SecurityContextHolder.getContext().authentication as? JwtAuthenticationToken
    val clusterAndSystem
        get() = system.split(":").let { parts ->
            if (parts.size == 3) "${parts[2]}:${parts[0]}" else system
        }

    val systemNavn get() = system.split(":").last()
    val systemAndNs get() = system.split(":").drop(1).joinToString(separator = ":")
    val cluster get() = system.split(":").first()
    private val erCC get() = stringClaim(IDTYP) == APP
    private val erObo get() = !erCC && oid != null
    val type
        get() = when {
            erObo -> OBO
            erCC -> CCF
            else -> UNAUTHENTICATED
        }

    val requiredAnsattId  get() =
        requireNotNull(ansattId) { "Mangler ansattId i OBO-token" }

    companion object {
        private const val GROUPS = "groups"
        const val APP = "app"
        const val OID = "oid"
        const val IDTYP = "idtyp"
        const val AZP_NAME = "azp_name"
        const val NAVIDENT = "NAVident"
    }
}
