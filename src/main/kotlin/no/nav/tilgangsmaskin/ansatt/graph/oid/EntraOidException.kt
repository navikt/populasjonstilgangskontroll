package no.nav.tilgangsmaskin.ansatt.graph.oid

import io.opentelemetry.api.trace.Span
import no.nav.tilgangsmaskin.ansatt.AnsattId
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.ErrorResponseException

class EntraOidException(ansattId: AnsattId, msg: String) : ErrorResponseException(NOT_FOUND) {
    init {
        body.title = "Uventet respons fra Entra"
        body.detail = msg
        body.properties = mapOf("navIdent" to ansattId.verdi,"traceId" to Span.current().spanContext.traceId)
    }
}