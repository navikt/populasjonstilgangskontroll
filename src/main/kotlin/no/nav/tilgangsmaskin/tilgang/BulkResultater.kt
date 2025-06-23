package no.nav.tilgangsmaskin.tilgang

import io.opentelemetry.api.trace.Span
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId

data class BulkResultater(val ansattId: AnsattId, val resultater: Set<BulkResultat>, val traceId: String = Span.current().spanContext.traceId) {
    data class BulkResultat(val brukerId: BrukerId, val status: Int, val body: Any? = null )
}