package no.nav.tilgangsmaskin.felles.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE
import org.springframework.http.ProblemDetail
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class OAuth2JsonAuthenticationEntryPoint(private val jsonMapper: JsonMapper) : AuthenticationEntryPoint {
    override fun commence(req: HttpServletRequest, res: HttpServletResponse, e: AuthenticationException) {
        res.status = UNAUTHORIZED.value()
        res.contentType = APPLICATION_PROBLEM_JSON_VALUE
        res.writer.use { jsonMapper.writeValue(it, ProblemDetail.forStatusAndDetail(UNAUTHORIZED, e.message ?: "Unauthorized")) }
    }
}
