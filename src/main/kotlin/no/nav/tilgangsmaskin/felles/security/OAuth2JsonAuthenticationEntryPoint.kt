package no.nav.tilgangsmaskin.felles.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE
import org.springframework.http.ProblemDetail.forStatusAndDetail
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

internal const val MANGLER_BEARER_TOKEN = "Mangler gyldig Bearer-token"

@Component
class OAuth2JsonAuthenticationEntryPoint(private val mapper: JsonMapper) : AuthenticationEntryPoint {
    override fun commence(req: HttpServletRequest, res: HttpServletResponse, e: AuthenticationException) =
        with(res)  {
            status = UNAUTHORIZED.value()
            contentType = APPLICATION_PROBLEM_JSON_VALUE
            mapper.writeValue(writer, forStatusAndDetail(UNAUTHORIZED, MANGLER_BEARER_TOKEN))
        }
}
