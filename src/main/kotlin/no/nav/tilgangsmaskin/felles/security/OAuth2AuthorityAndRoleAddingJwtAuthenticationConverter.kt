package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.APP
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.IDTYP
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.NAVIDENT
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.OID
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD_GCP
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.convert.converter.Converter
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

private const val ENKELT_ROLE = "ROLE_ENKELT"
const val TOKEN_TYPE_AUTHORITY_PREFIX = "TOKEN_"
const val OBO_AUTHORITY = "${TOKEN_TYPE_AUTHORITY_PREFIX}OBO"
const val CCF_AUTHORITY = "${TOKEN_TYPE_AUTHORITY_PREFIX}CCF"
private const val ROLE = "ROLE_"
const val ROLES_CLAIM = "roles"

@Component
class OAuth2AuthorityAndRoleAddingJwtAuthenticationConverter(
    private val env: Environment,
    @Value($$"${gruppe.enkelttilgang:}") private val gruppeEnkeltTilgang: String) : Converter<Jwt, AbstractAuthenticationToken> {

    private val delegate = JwtAuthenticationConverter()
        .andThen {
            val jwt = it as JwtAuthenticationToken
            val authorities = linkedSetOf<GrantedAuthority>()
            authorities += jwt.authorities
            authorities += roleAuthorities(jwt.token)
            tokenTypeAuthority(jwt.token)?.let(authorities::add)
            if (shouldAddEnkeltRole(jwt.token)) {
                authorities += SimpleGrantedAuthority(ENKELT_ROLE)
            }
            JwtAuthenticationToken(jwt.token, principal(jwt.token, authorities), authorities)
        }

    override fun convert(jwt: Jwt): AbstractAuthenticationToken =
        delegate.convert(jwt) ?: throw IllegalArgumentException("JWT konvertering feilet for token med claims: ${jwt.claims}")

    private fun shouldAddEnkeltRole(jwt: Jwt): Boolean {
        val roles = jwt.getClaimAsStringList(ROLES_CLAIM).orEmpty()
        val isProd = env.acceptsProfiles(Profiles.of(PROD_GCP))
        return !isProd || gruppeEnkeltTilgang in roles
    }

    private fun principal(jwt: Jwt, authorities: Set<GrantedAuthority>) =
        DefaultOAuth2AuthenticatedPrincipal(
            jwt.subject ?: jwt.getClaimAsString(NAVIDENT) ?: "unknown",
            jwt.claims,
            authorities
        )

    private fun tokenTypeAuthority(jwt: Jwt) =
        when {
            jwt.getClaimAsString(IDTYP) == APP -> SimpleGrantedAuthority(CCF_AUTHORITY)
            jwt.getClaimAsString(OID) != null -> SimpleGrantedAuthority(OBO_AUTHORITY)
            else -> null
        }

    private fun roleAuthorities(jwt: Jwt) =
        buildSet {
            jwt.getClaimAsStringList(ROLES_CLAIM).orEmpty().forEach { role ->
                add(SimpleGrantedAuthority(role.takeIf { it.startsWith(ROLE) } ?: "$ROLE$role"))
            }
        }
}