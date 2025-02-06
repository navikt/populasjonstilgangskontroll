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
        if (res.statusCode.is4xxClientError) throw IrrecoverableException(res.statusCode, uri = req.uri)
        else throw RecoverableException(res.statusCode, uri = req.uri) // TODO: Håndter 5xx feil bedre
    }
}

open class IrrecoverableException(status: HttpStatusCode, detail: String? = "Fikk respons $status", extras: Map<String,Any> = emptyMap(), cause: Throwable? = null) :
    ErrorResponseException(status, problemDetail(status, detail, extras), cause)  {
    constructor(status: HttpStatusCode, detail: String? = "Fikk respons $status", uri: URI? = null, cause: Throwable? = null) : this(status, detail, uri.toMap(), cause)
}

open class RecoverableException(status: HttpStatusCode, detail: String? = "Fikk respons $status", extras: Map<String,Any> = emptyMap(), cause: Throwable? = null) :
    ErrorResponseException(status, problemDetail(status, detail, extras), cause)  {
    constructor(status: HttpStatusCode, detail: String? = "Fikk respons $status", uri: URI? = null,cause: Throwable? = null) : this(status, detail, uri.toMap(), cause)

}

private fun problemDetail(status: HttpStatusCode,
                          detail: String?,
                          extras: Map<String,Any> = emptyMap()) =
    forStatusAndDetail(status, detail).apply {
        title = resolve(status.value())?.reasonPhrase ?: "$status"
        extras.forEach { (key, value) -> setProperty(key,value) }
    }

private fun URI?.toMap() =this?.let { mapOf("uri" to it) } ?: emptyMap()