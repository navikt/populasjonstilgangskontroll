package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.rest.Token.Companion.AZP_NAME
import no.nav.tilgangsmaskin.felles.security.OAuth2AuthorityAddingJwtAuthenticationConverter.Companion.SYSTEM_AUTHORITY_PREFIX
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.OSLO
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class OAuth2AuthorityAddingJwtAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken> {

    private val delegate = JwtAuthenticationConverter()
    private val log = getLogger(javaClass)

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val jwtToken = delegate.convert(jwt) as JwtAuthenticationToken
        val authorities = linkedSetOf(*jwtToken.authorities.toTypedArray())
        systemAuthority(jwtToken.token.getClaimAsString(AZP_NAME))?.let(authorities::add)
        return JwtAuthenticationToken(jwtToken.token, authorities, jwtToken.name).also {
            log.trace(
                "JWT validert OK: system={}, authorities={}, expiresAt={}",
                jwt.getClaimAsString(AZP_NAME),
                it.authorities.map { a -> a.authority },
                jwt.expiresAt?.atZone(OSLO))
        }
    }


    companion object {
        const val SYSTEM_AUTHORITY_PREFIX = "SYSTEM_"

        fun systemAuthority(azpName: String?) =
            azpName?.split(":")?.lastOrNull()?.takeIf { it.isNotBlank() }?.let { systemNavn ->
                SystemAuthority(systemNavn)
            }
    }
}

data class SystemAuthority(private val system: String) : GrantedAuthority {
    override fun getAuthority() = "$SYSTEM_AUTHORITY_PREFIX$system"
}