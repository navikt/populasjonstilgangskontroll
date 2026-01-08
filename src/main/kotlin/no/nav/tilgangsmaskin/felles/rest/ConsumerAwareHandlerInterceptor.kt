package no.nav.tilgangsmaskin.felles.rest

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.tilgangsmaskin.regler.motor.TokenTypeTeller
import no.nav.tilgangsmaskin.tilgang.Token
import no.nav.tilgangsmaskin.tilgang.TokenType
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class ConsumerAwareHandlerInterceptor(private val token: Token,  private val teller: TokenTypeTeller,private val registry: MeterRegistry) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        MDC.put(CONSUMER_ID, token.systemAndNs)
        registry.counter(METRIC, Tags.of("remote_system",token.systemNavn)).increment()
        if (request.requestURI.contains("bulk")) {
            tell("bulk")
        }
        else {
            tell("enkelt")
        }
        return true
    }

    private fun tell(type: String) =
        teller.tell(Tags.of("type",type,"token",TokenType.from(token).name.lowercase()))

    companion object  {
        private const val METRIC = "http_requests_by_remote_system"
        const val CONSUMER_ID = "consumerId"
        const val USER_ID = "userId"
    }
}