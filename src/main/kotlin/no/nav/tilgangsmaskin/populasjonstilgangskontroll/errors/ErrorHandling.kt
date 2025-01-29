package no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.GraphQLErrorHandler
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
class DefaultGraphQlErrorHandler : GraphQLErrorHandler


@Component
@Primary
class DefaultRestErrorHandler : ErrorHandler {
    override fun handle(req: HttpRequest, res: ClientHttpResponse) {
        if (res.statusCode.is4xxClientError) IrrecoverableException(res.statusCode, req.uri)
        else throw RecoverableException(res.statusCode, req.uri) // TODO: Håndter 5xx feil bedre
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