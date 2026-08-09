package no.nav.tilgangsmaskin.tilgang

import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

abstract class TilgangControllerBase {

    protected val log = getLogger(javaClass)

    protected fun sjekk(predikat: Boolean, status: HttpStatus, message: String) {
        if (!predikat) throw ResponseStatusException(status, message)
    }

    companion object {
        val UNPROTECTED_ENDPOINTS = arrayOf("/$DEV/**","/swagger-ui/**", "/v3/api-docs/**", "/monitoring/**")
        const val PROD_BASE_PATH = "/api/v1"
    }
}
