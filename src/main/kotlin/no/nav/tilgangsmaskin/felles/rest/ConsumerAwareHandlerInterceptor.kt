package no.nav.tilgangsmaskin.felles.rest

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class ConsumerAwareHandlerInterceptor(private val accessor: Token) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        MDC.put(USER_ID, accessor.ansattId?.verdi ?: "N/A")
        MDC.put(CONSUMER_ID, accessor.systemAndNs)
        return true
    }
    companion object  {
        const val CONSUMER_ID = "consumerId"
        const val USER_ID = "userId"
    }
}