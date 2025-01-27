package no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors

import org.springframework.context.annotation.Primary
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus.*
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail.forStatusAndDetail
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.ErrorResponseException
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import java.net.URI


@Component
@Primary
class DefaultErrorHandler : ErrorHandler {
    override fun handle(req: HttpRequest, res: ClientHttpResponse) {
        throw when (val code = res.statusCode) {
            BAD_REQUEST, NOT_FOUND -> IrrecoverableException(code, req.uri)
            else -> RecoverableException(code, req.uri)
        }
    }
}

open class IrrecoverableException(status: HttpStatusCode,
                                  uri: URI,
                                  detail: String? = null,
                                  cause: Throwable? = null,
                                  stackTrace: String? = null) :
    ErrorResponseException(status, problemDetail(status, detail, uri, stackTrace), cause) {

    class NotFoundException(uri: URI,
                            detail: String? = NOT_FOUND.reasonPhrase,
                            cause: Throwable? = null,
                            stackTrace: String? = null) :
        IrrecoverableException(NOT_FOUND, uri, detail, cause, stackTrace)

    override fun toString() = "IrrecoverableException(status=${statusCode}, detail=$body"
}

open class RecoverableException(status: HttpStatusCode,
                                uri: URI,
                                detail: String? = "Fikk respons $status",
                                cause: Throwable? = null) :
    ErrorResponseException(status, problemDetail(status, detail, uri), cause)

private fun problemDetail(status: HttpStatusCode,
                          detail: String?,
                          uri: URI,
                          stackTrace: String? = null) =
    forStatusAndDetail(status, detail).apply {
        title = resolve(status.value())?.reasonPhrase ?: "$status"
        setProperty("uri", uri)
        stackTrace?.let { setProperty("stackTrace", it) }
    }