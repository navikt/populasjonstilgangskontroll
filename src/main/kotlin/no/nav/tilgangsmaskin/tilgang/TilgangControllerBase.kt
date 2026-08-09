package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.TilgangControllerBase.Companion.PROD_BASE_PATH
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@Target(CLASS)
@Retention(RUNTIME)
@SecurityScheme(bearerFormat = "JWT", name = "bearerAuth", scheme = "bearer", type = HTTP)
@RestController
@RequestMapping(PROD_BASE_PATH)
@SecurityRequirement(name = "bearerAuth")
annotation class TilgangApiController

abstract class TilgangControllerBase(protected val token: Token) {

    protected val log = getLogger(javaClass)

    protected fun sjekk(predikat: Boolean, status: HttpStatus, message: String) {
        if (!predikat) throw ResponseStatusException(status, message)
    }

    companion object {
        val UNPROTECTED_ENDPOINTS = arrayOf("/$DEV/**","/swagger-ui/**", "/v3/api-docs/**", "/monitoring/**")
        const val PROD_BASE_PATH = "/api/v1"
    }
}
