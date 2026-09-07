package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.rest.PROD_BASE_PATH
import tools.jackson.databind.json.JsonMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.tilgangsmaskin.regler.enkelttilgang.ENKELTTILGANG_PATH
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component


@Component
class OAuth2JsonAccessDeniedHandler(private val mapper: JsonMapper, private val authContext: AuthContext) : AccessDeniedHandler {
    private val log = getLogger(javaClass)

    override fun handle(req: HttpServletRequest, res: HttpServletResponse, e: AccessDeniedException) {
        if (req.requestURI == ENKELTTILGANG_PATH) {
            log.info("Enkelttilgang avvist, ${authContext.ansattId} er ikke medlem av GA-Enkelttilgang")
        }
        with(res) {
            status = FORBIDDEN.value()
            contentType = APPLICATION_PROBLEM_JSON_VALUE
            mapper.writeValue(writer, securityProblemDetail(FORBIDDEN, e.message ?: "Access Denied"))
        }
    }
}