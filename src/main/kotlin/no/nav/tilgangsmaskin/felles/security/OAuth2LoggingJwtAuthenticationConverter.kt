package no.nav.tilgangsmaskin.felles.security

import jakarta.servlet.http.HttpServletRequest
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.AZP_NAME
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class OAuth2LoggingJwtAuthenticationConverter(
    private val delegate: Converter<Jwt, AbstractAuthenticationToken> = JwtAuthenticationConverter()
) : Converter<Jwt, AbstractAuthenticationToken> {

    private val log = getLogger(javaClass)

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val sub = jwt.subject
        val iss = jwt.issuer
        val aud = jwt.audience
        val exp = jwt.expiresAt
        val system = jwt.getClaimAsString(AZP_NAME)
        val request = currentRequest()
        val url = request?.requestURI
        return runCatching { delegate.convert(jwt) }
            .onSuccess { log.trace("JWT validert OK: sub={}, system={}, iss={}, aud={}, expiresAt={}, authorities={}, url={}", sub, system, iss, aud, exp, it.authorities, url) }
            .onFailure { log.warn("JWT konvertering feilet: sub=$sub, system=$system, iss=$iss, url=$url, feil=${it.message}", it) }
            .getOrThrow()
    }

    private fun currentRequest(): HttpServletRequest? =
        (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
}
