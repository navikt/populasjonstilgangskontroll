package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.CLIENT_CREDENTIALS
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.GROUPS
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.NAVIDENT
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.OID
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.ROLES
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD_GCP_PROFILE
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.convert.converter.Converter
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import java.util.UUID

private const val ROLLE = "ROLE_"
private const val ENKELTGRUPPE_ROLLE = "${ROLLE}ENKELT"
private const val PREFIX = "TOKEN_"
const val OBO_AUTHORITY = "${PREFIX}OBO"
const val CCF_AUTHORITY = "${PREFIX}CCF"

@Component
class OAuth2AuthorityAndRoleAddingJwtAuthenticationConverter(private val env: Environment,
    @param:Value($$"${gruppe.enkelttilgang:}") private val gruppeEnkeltTilgang: UUID) : Converter<Jwt, AbstractAuthenticationToken> {

    private val log = getLogger(javaClass)

    private val delegate = JwtAuthenticationConverter()
        .andThen {
            val jwt = it as JwtAuthenticationToken
            val authorities = buildSet {
                addAll(jwt.authorities)
                addAll(roller(jwt.token))
                authority(jwt.token)?.let(::add)
                if (shouldAddEnkeltRole(jwt.token.getClaimAsStringList(GROUPS))) add(SimpleGrantedAuthority(ENKELTGRUPPE_ROLLE))
            }
            JwtAuthenticationToken(jwt.token, principal(jwt.token, authorities), authorities)
        }

    override fun convert(jwt: Jwt)  =
        delegate.convert(jwt) ?: error("JWT konvertering feilet for token med claims: ${jwt.claims}")

    private fun shouldAddEnkeltRole(groups: List<String>?)  =
        !env.acceptsProfiles(PROD_GCP_PROFILE) || "$gruppeEnkeltTilgang" in groups.orEmpty()

    private fun principal(jwt: Jwt, authorities: Set<GrantedAuthority>) =
        DefaultOAuth2AuthenticatedPrincipal(
            jwt.subject ?: jwt.getClaimAsString(NAVIDENT) ?: "unknown",
            jwt.claims, authorities).also {
                log.trace("Principal satt til {} med authorities: {}", it.name, it.authorities)
            }

    private fun authority(jwt: Jwt) =
        when {
            jwt.getClaimAsStringList(ROLES).orEmpty().contains(CLIENT_CREDENTIALS) -> SimpleGrantedAuthority(CCF_AUTHORITY)
            jwt.getClaimAsString(OID) != null -> SimpleGrantedAuthority(OBO_AUTHORITY)
            else -> null
        }

    private fun roller(jwt: Jwt) =
        buildSet {
            jwt.getClaimAsStringList(ROLES).orEmpty().forEach { rolle ->
                add(SimpleGrantedAuthority(rolle.takeIf {
                    it.startsWith(ROLLE)
                } ?: "$ROLLE$rolle"))
            }
        }
}