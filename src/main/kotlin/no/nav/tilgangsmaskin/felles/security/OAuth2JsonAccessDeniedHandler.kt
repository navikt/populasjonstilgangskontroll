package no.nav.tilgangsmaskin.felles.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class OAuth2JsonAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(request: HttpServletRequest, response: HttpServletResponse, accessDeniedException: AccessDeniedException) {
        response.status = FORBIDDEN.value()
        response.contentType = APPLICATION_JSON_VALUE
        response.writer.write(
            """{"timestamp":"${OffsetDateTime.now()}","status":${FORBIDDEN.value()},"error":"${FORBIDDEN.reasonPhrase}","message":"${escape(
                accessDeniedException.message ?: "Access denied"
            )}"}"""
        )
    }

    private fun escape(message: String) = buildString(message.length) {
        message.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(char)
            }
        }
    }
}
