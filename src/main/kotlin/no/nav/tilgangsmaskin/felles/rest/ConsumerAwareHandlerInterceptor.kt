package no.nav.tilgangsmaskin.felles.rest

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangConfig
import no.nav.tilgangsmaskin.tilgang.DEFAULT_PREFIX
import no.nav.tilgangsmaskin.tilgang.Token
import no.nav.tilgangsmaskin.tilgang.TokenType
import no.nav.tilgangsmaskin.tilgang.TokenType.OBO
import org.springframework.http.HttpStatus.FORBIDDEN
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.server.ResponseStatusException

@Component
class ConsumerAwareHandlerInterceptor(
    private val token: Token,
    private val registry: MeterRegistry,
    private val config: EnkeltTilgangConfig,
) :
    HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val konsument = token.systemNavn
        
        if (request.requestURI.endsWith("$DEFAULT_PREFIX/overstyr") && token.type == OBO && konsument !in config.systemer) {
            val tillatte = config.systemer.joinToString(", ")
            throw ResponseStatusException(
                FORBIDDEN,
                "Konsument $konsument har ikke tilgang til enkelttilgang, kun $tillatte",
            )
        }

        MDC.put(CONSUMER_ID, token.systemAndNs)
        token.ansattId?.verdi?.let { MDC.put(USER_ID, it) }
        registry.counter(METRIC, Tags.of("remote_system", konsument)).increment()
        return true
    }


    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        MDC.remove(CONSUMER_ID)
        MDC.remove(USER_ID)
    }

    companion object {
        private const val METRIC = "http_requests_by_remote_system"
        const val CONSUMER_ID = "consumerId"
        const val USER_ID = "userId"
    }
}