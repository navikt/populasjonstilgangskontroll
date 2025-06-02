package no.nav.tilgangsmaskin.felles.rest

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class ConsumerAwareHandlerInterceptor(private val token: Token) : HandlerInterceptor {
    private val log = getLogger(javaClass)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        MDC.put(USER_ID, token.ansattId?.verdi ?: "N/A")
        MDC.put(CONSUMER_ID, token.systemAndNs)
        log.info("X-forwarded-host: ${request.getHeader("X-Forwarded-Host")}, X-forwarded-for: ${request.getHeader("X-Forwarded-For")}")
        return true
    }
    companion object  {
        const val CONSUMER_ID = "consumerId"
        const val USER_ID = "userId"
    }
}