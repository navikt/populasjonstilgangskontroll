package no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.GraphQLErrorHandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.LoggingGraphQLInterceptor
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
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
    private val log = LoggerFactory.getLogger(DefaultRestErrorHandler::class.java)

    override fun handle(req: HttpRequest, res: ClientHttpResponse) {
        if (res.statusCode.is4xxClientError) throw IrrecoverableRestException(res.statusCode, req.uri, res.statusText).also {
            log.warn("Irrecoverable etter ${res.statusCode.value()} fra ${req.uri}")
        }
        else throw RecoverableRestException(res.statusCode, req.uri, res.statusText).also {
            log.warn("Reecoverable etter ${res.statusCode.value()} fra ${req.uri}")
        }
    }
}

open class IrrecoverableRestException(status: HttpStatusCode,
                                      uri: URI,
                                      msg: String = (status as HttpStatus).reasonPhrase,
                                      cause: Throwable? = null) :  ErrorResponseException(status, problemDetail(status, msg,uri), cause)

open class RecoverableRestException(status: HttpStatusCode,
                                    uri: URI,
                                    msg: String = (status as HttpStatus).reasonPhrase,
                                    cause: Throwable? = null) :  ErrorResponseException(status, problemDetail(status, msg,uri), cause)

private fun problemDetail(status: HttpStatusCode, msg: String, uri: URI) =
    forStatusAndDetail(status, msg).apply {
        title = "${status.value()}"
        properties = mapOf("uri" to "$uri")
    }

