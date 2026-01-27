package no.nav.tilgangsmaskin.ansatt.graph

import io.opentelemetry.api.trace.Span
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.ErrorResponseException

class EntraOidException(ansattId: String, msg: String) : ErrorResponseException(NOT_FOUND) {
    init {
        body.title = TITLE
        body.detail = msg
        body.properties = mapOf("navIdent" to ansattId,"traceId" to Span.current().spanContext.traceId)
    }

    companion object   {
        const val TITLE = "Uventet respons fra Entra"
    }
}