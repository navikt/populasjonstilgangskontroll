package no.nav.tilgangsmaskin.felles.security

import tools.jackson.databind.json.JsonMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now


@Component
class OAuth2JsonAccessDeniedHandler(private val mapper: JsonMapper) : AccessDeniedHandler {
    override fun handle(req: HttpServletRequest, res: HttpServletResponse, e: AccessDeniedException) {
        res.status = FORBIDDEN.value()
        res.contentType = APPLICATION_JSON_VALUE
        res.writer.use { mapper.writeValue(it,
            AccessDeniedResponse(FORBIDDEN.value(), FORBIDDEN.reasonPhrase, e.message ?: "Access denied")
        )}
    }
    private data class AccessDeniedResponse(val status: Int,
                                            val error: String,
                                            val message: String,
                                            val timestamp: OffsetDateTime = now())
}
