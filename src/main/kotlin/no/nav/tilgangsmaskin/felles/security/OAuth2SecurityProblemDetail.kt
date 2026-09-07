package no.nav.tilgangsmaskin.felles.security

import io.opentelemetry.api.trace.Span
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata.Companion.TYPE_URI
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail.forStatusAndDetail

fun securityProblemDetail(status: HttpStatus, detail: String) =
    forStatusAndDetail(status, detail).apply {
        type = TYPE_URI
        title = "${status.value()}"
        properties = mapOf("traceId" to Span.current().spanContext.traceId)
    }
