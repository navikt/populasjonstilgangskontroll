package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.rest.Token.Companion.AZP_NAME
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.web.context.request.RequestContextHolder.getRequestAttributes
import org.springframework.web.context.request.ServletRequestAttributes

class OAuth2LoggingJwtAuthenticationConverter(
    private val delegate: Converter<Jwt, AbstractAuthenticationToken> = JwtAuthenticationConverter()
) : Converter<Jwt, AbstractAuthenticationToken> {

    private val log = getLogger(javaClass)

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val exp = jwt.expiresAt
        val system = jwt.getClaimAsString(AZP_NAME)
        val request = (getRequestAttributes() as? ServletRequestAttributes)?.request
        val url = request?.requestURI
        return runCatching { delegate.convert(jwt) }
            .onSuccess { log.trace("JWT validert OK: system={}, expiresAt={}, authorities={}, url={}", system, exp, it.authorities, url) }
            .onFailure { log.warn("JWT konvertering feilet: system=$system, url=$url, feil=${it.message}", it) }
            .getOrThrow()
    }

}
