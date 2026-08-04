package no.nav.tilgangsmaskin.felles.security

import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

class OAuth2LoggingJwtAuthenticationConverter(
    private val delegate: Converter<Jwt, AbstractAuthenticationToken> = JwtAuthenticationConverter()
) : Converter<Jwt, AbstractAuthenticationToken> {

    private val log = getLogger(javaClass)

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val sub = jwt.subject
        val iss = jwt.issuer
        val aud = jwt.audience
        val exp = jwt.expiresAt

        return runCatching { delegate.convert(jwt) }
            .onSuccess { log.trace("JWT validert OK: sub={}, iss={}, aud={}, expiresAt={}, authorities={}", sub, iss, aud, exp, it.authorities) }
            .onFailure { log.warn("JWT konvertering feilet: sub=$sub, iss=$iss, feil=${it.message}",it) }
            .getOrThrow()
    }
}
