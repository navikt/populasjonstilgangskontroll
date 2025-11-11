package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.cache.CacheAdapter.Companion.VALKEY
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail.forStatusAndDetail
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.ErrorResponseException
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import java.net.URI

@Component
@Primary
class DefaultRestErrorHandler : ErrorHandler {
    private val log = getLogger(javaClass)

    override fun handle(req: HttpRequest, res: ClientHttpResponse) {
        when {
            res.statusCode == NOT_FOUND -> {
                log.info("Irrecoverable exception etter ${res.statusCode.value()} fra ${req.uri}")
                throw IrrecoverableRestException(res.statusCode, req.uri, res.statusText)
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
}

open class IrrecoverableRestException(
        status: HttpStatusCode, uri: URI, msg: String = (status as HttpStatus).reasonPhrase,
        cause: Throwable? = null) : ErrorResponseException(status, problemDetail(status, msg, uri), cause)

open class RecoverableRestException(
        status: HttpStatusCode,
        uri: URI,
        msg: String = (status as HttpStatus).reasonPhrase,
        cause: Throwable? = null) : ErrorResponseException(status, problemDetail(status, msg, uri), cause)

private fun problemDetail(status: HttpStatusCode, msg: String, uri: URI) =
    forStatusAndDetail(status, msg).apply {
        title = "${status.value()}"
        properties = mapOf("uri" to "$uri")
    }

