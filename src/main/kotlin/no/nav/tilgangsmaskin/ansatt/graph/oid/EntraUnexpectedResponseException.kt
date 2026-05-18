package no.nav.tilgangsmaskin.ansatt.graph.oid

import io.opentelemetry.api.trace.Span
import no.nav.tilgangsmaskin.ansatt.AnsattId
import org.springframework.http.HttpStatus
import org.springframework.web.ErrorResponseException

class EntraUnexpectedResponseException(ansattId: AnsattId, msg: String, status: HttpStatus) : ErrorResponseException(status) {
    init {
        body.title = "Uventet respons fra Entra"
        body.detail = msg
        body.properties = mapOf("navIdent" to ansattId.verdi,"traceId" to Span.current().spanContext.traceId)
    }
}