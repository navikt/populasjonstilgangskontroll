package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.instrument.Tags
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.tilgangsmaskin.tilgang.Token.Companion.AAD_ISSUER
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@Target(CLASS)
@Retention(RUNTIME)
@SecurityScheme(bearerFormat = "JWT", name = "bearerAuth", scheme = "bearer", type = HTTP)
@ProtectedRestController(value = ["/api/v1"], issuer = AAD_ISSUER, claimMap = [])
@SecurityRequirement(name = "bearerAuth")
annotation class TilgangApiController

abstract class TilgangControllerBase(
    protected val token: Token,
    private val teller: TokenTypeTeller) {

    protected val log = getLogger(javaClass)

    protected fun <T> withAnsattContext(ansattId: AnsattId, block: () -> T): T {
        MDC.put(USER_ID, ansattId.verdi)
        return block()
    }

    protected fun tell(type: String, tokenType: TokenType) =
        teller.tell(Tags.of("type", type, "token", tokenType.name.lowercase()))

    protected fun sjekk(predikat: Boolean, status: HttpStatus, message: String) {
        if (!predikat) throw ResponseStatusException(status, message)
    }

    protected fun ansattIdFraToken()  =
        requireNotNull(token.ansattId) { "Mangler ansattId i OBO-token" }
}
