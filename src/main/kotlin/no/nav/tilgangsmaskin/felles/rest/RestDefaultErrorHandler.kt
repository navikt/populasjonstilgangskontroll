package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

class RestDefaultErrorHandler : ErrorHandler {
    private val log = getLogger(javaClass)

    override fun handle(req: HttpRequest, res: ClientHttpResponse) {
        val ident = req.headers.getFirst(IDENTIFIKATOR)

        when {
            res.statusCode == NOT_FOUND -> {
                log.info("Not found exception etter ${res.statusCode.value()} fra ${req.uri}")
                throw NotFoundRestException(req.uri, ident)
            }
            res.statusCode.is4xxClientError -> {
                log.warn("Irrecoverable exception etter ${res.statusCode.value()} fra ${req.uri}")
                throw IrrecoverableRestException(res.statusCode, req.uri, res.statusText)
            }
            else -> {
                log.warn("Recoverable exception etter ${res.statusCode.value()} fra ${req.uri}")
                throw RecoverableRestException(res.statusCode, req.uri, res.statusText)
            }
        }
    }

    companion object {
        const val IDENTIFIKATOR = "X-Identifikator"
    }

}