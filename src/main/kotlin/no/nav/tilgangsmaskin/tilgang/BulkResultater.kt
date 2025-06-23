package no.nav.tilgangsmaskin.tilgang

import io.opentelemetry.api.trace.Span
import net.minidev.json.annotate.JsonIgnore
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.http.HttpStatus

data class BulkResultater(val ansattId: AnsattId, val resultater: Set<BulkResultat>, val traceId: String = Span.current().spanContext.traceId) {
    data class BulkResultat(val brukerId: BrukerId, @JsonIgnore val httpStatus: HttpStatus, val body: Any? = null ) {
        val status = httpStatus.value()
    }
}