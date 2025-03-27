package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.tilgang1.TokenClaimsAccessor
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class ConsumerAwareHandlerInterceptor(private val accessor: TokenClaimsAccessor) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        MDC.put("userid", accessor.ansattId?.verdi ?: "N/A")
        MDC.put("consumerid", accessor.systemAndNamespace)
        return true
    }
}