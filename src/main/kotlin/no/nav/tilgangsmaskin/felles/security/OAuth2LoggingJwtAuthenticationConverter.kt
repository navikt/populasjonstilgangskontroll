package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.rest.Token.Companion.AZP_NAME
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.OSLO
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.web.context.request.RequestContextHolder.getRequestAttributes
import org.springframework.web.context.request.ServletRequestAttributes

class OAuth2LoggingJwtAuthenticationConverter(
    private val delegate: Converter<Jwt, AbstractAuthenticationToken> = JwtAuthenticationConverter()) : Converter<Jwt, AbstractAuthenticationToken> {

    private val log = getLogger(javaClass)

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val exp = jwt.expiresAt?.atZone(OSLO)
        val system = jwt.getClaimAsString(AZP_NAME)
        val uri = (getRequestAttributes() as? ServletRequestAttributes)?.request?.requestURI
        return runCatching { delegate.convert(jwt) }
            .onSuccess { log.trace("JWT validert OK: system={}, expiresAt={}, url={}", system, exp, uri) }
            .onFailure { log.warn("JWT konvertering feilet: system=$system, uri=$uri, feil=${it.message}", it) }
            .getOrThrow()
    }

}
