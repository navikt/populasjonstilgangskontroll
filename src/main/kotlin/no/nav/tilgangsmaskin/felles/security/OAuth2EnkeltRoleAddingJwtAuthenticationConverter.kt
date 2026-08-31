package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD_GCP
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.convert.converter.Converter
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

private const val ENKELT_ROLE = "ROLE_ENKELT"

@Component
class OAuth2EnkeltRoleAddingJwtAuthenticationConverter(private val env: Environment, @Value($$"${gruppe.enkelttilgang:}") private val gruppeEnkeltTilgang: String) : Converter<Jwt, AbstractAuthenticationToken> {
    override fun convert(source: Jwt): AbstractAuthenticationToken {
        val roles = source.getClaimAsStringList("roles").orEmpty()
        val isProd = env.acceptsProfiles(Profiles.of(PROD_GCP))

        val authorities = if (!isProd || gruppeEnkeltTilgang in roles) {
            listOf(SimpleGrantedAuthority(ENKELT_ROLE))
        } else {
            emptyList()
        }

        return JwtAuthenticationToken(source, authorities)
    }
}