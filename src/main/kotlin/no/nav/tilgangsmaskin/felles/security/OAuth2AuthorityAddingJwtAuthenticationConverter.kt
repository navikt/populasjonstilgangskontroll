package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.rest.Token.Companion.APP
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.IDTYP
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.NAVIDENT
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.OID
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

const val TOKEN_TYPE_AUTHORITY_PREFIX = "TOKEN_"
const val OBO_AUTHORITY = "${TOKEN_TYPE_AUTHORITY_PREFIX}OBO"
const val CCF_AUTHORITY = "${TOKEN_TYPE_AUTHORITY_PREFIX}CCF"
private const val ROLE = "ROLE_"
const val ROLES_CLAIM = "roles"

class OAuth2AuthorityAddingJwtAuthenticationConverter(
    private val extraAuthorities: Set<GrantedAuthority> = emptySet(),
) : Converter<Jwt, AbstractAuthenticationToken> {

    private val delegate = JwtAuthenticationConverter()
        .andThen {
            val jwt = it as JwtAuthenticationToken
            val authorities = linkedSetOf(*jwt.authorities.toTypedArray(), *extraAuthorities.toTypedArray())
            authorities.addAll(roleAuthorities(jwt.token))
            tokenTypeAuthority(jwt.token)?.let(authorities::add)
            JwtAuthenticationToken(jwt.token, principal(jwt.token, authorities), authorities)
        }

    override fun convert(jwt: Jwt): AbstractAuthenticationToken =
        delegate.convert(jwt) ?: throw IllegalArgumentException("JWT konvertering feilet for token med claims: ${jwt.claims}")

    companion object {
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
            jwt.getClaimAsStringList(ROLES_CLAIM).orEmpty().mapTo(linkedSetOf()) { role ->
                SimpleGrantedAuthority(role.takeIf { it.startsWith(ROLE) } ?: "$ROLE$role")
            }
    }
}
