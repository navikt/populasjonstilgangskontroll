package no.nav.tilgangsmaskin.felles.rest

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class ConsumerAwareHandlerInterceptor(private val accessor: Token, private val registry: MeterRegistry) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        MDC.put(USER_ID, accessor.ansattId?.verdi ?: "N/A")
        MDC.put(CONSUMER_ID, accessor.systemAndNs)
        val forwardedHost = request.getHeader("X-Forwarded-Host")
        registry.counter(METRIC, Tags.of("remote_host", forwardedHost)).increment()
        return true
    }
    companion object  {
        private const val METRIC = "http_requests_by_remote_host"
        const val CONSUMER_ID = "consumerId"
        const val USER_ID = "userId"
    }
}