package no.nav.tilgangsmaskin.felles.security

import jakarta.servlet.http.HttpServletRequest
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
        val request = currentRequest()
        val url = request?.requestURI
        val caller = request?.getHeader("X-Forwarded-For") ?: request?.remoteAddr

        return runCatching { delegate.convert(jwt) }
            .onSuccess { log.trace("JWT validert OK: sub={}, iss={}, aud={}, expiresAt={}, authorities={}, url={}, caller={}", sub, iss, aud, exp, it.authorities, url, caller) }
            .onFailure { log.warn("JWT konvertering feilet: sub=$sub, iss=$iss, url=$url, caller=$caller, feil=${it.message}", it) }
            .getOrThrow()
    }

    private fun currentRequest(): HttpServletRequest? =
        (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
}
