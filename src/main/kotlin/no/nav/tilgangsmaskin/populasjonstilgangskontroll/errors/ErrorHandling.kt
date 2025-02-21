package no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.GraphQLErrorHandler
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus.resolve
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
        if (res.statusCode.is4xxClientError) throw IrrecoverableException(res.statusCode, req.uri,res.statusText)
        else throw RecoverableException(res.statusCode,  req.uri,res.statusText) // TODO: Håndter 5xx feil bedre
    }
}

open class IrrecoverableException(status: HttpStatusCode, detail: String, extras: Map<String,Any> = emptyMap(), title: String ?= null,cause: Throwable? = null) :
    ErrorResponseException(status, problemDetail(status, detail, extras, title), cause)  {
    constructor(status: HttpStatusCode,
                uri: URI? = null,
                detail: String,
                title: String? = null,
                cause: Throwable? = null) : this(status, detail, uri.toMap(), title,cause)
}

open class RecoverableException(status: HttpStatusCode, detail: String,extras: Map<String,Any> = emptyMap(), title: String ?= null, cause: Throwable? = null) :
    ErrorResponseException(status, problemDetail(status, detail, extras, title), cause)  {
    constructor(status: HttpStatusCode,
                uri: URI? = null,
                detail: String,
                title: String? = null,
                cause: Throwable? = null) : this(status, detail, uri.toMap(), title,cause)

}

private fun problemDetail(status: HttpStatusCode,
                          detail: String,
                          extras: Map<String,Any> = emptyMap(),
                          tittel: String? = null) =
    forStatusAndDetail(status, detail).apply {
        title = tittel
        type = URI.create("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett")
        extras.forEach { (key, value) -> setProperty(key,value) }
    }

private fun URI?.toMap() =this?.let { mapOf("uri" to it) } ?: emptyMap()