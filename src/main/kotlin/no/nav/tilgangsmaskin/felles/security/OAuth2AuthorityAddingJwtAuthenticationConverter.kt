package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.rest.Token.Companion.AZP_NAME
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken


private const val SYSTEM_AUTHORITY_PREFIX = "SYSTEM_"

class OAuth2AuthorityAddingJwtAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken> {

    private val delegate = JwtAuthenticationConverter()
        .andThen {
            val jwt = it as JwtAuthenticationToken
            val authorities = linkedSetOf(*jwt.authorities.toTypedArray())
            jwt.token.getClaimAsString(AZP_NAME)?.let { tilSystemAuthority(it) }?.let(authorities::add)
            JwtAuthenticationToken(jwt.token, authorities, jwt.name)
        }

    override fun convert(jwt: Jwt): AbstractAuthenticationToken =
        delegate.convert(jwt) ?: throw IllegalArgumentException("JWT konvertering feilet for token med claims: ${jwt.claims}")

    companion object {

        fun tilSystemAuthority(azpName: String)  =
            azpName.split(":").lastOrNull()?.takeIf { it.isNotBlank() }?.let { SystemAuthority(it) }

    }
    data class SystemAuthority(private val system: String) : GrantedAuthority {
        override fun getAuthority() = "$SYSTEM_AUTHORITY_PREFIX$system"
    }
}

