package no.nav.tilgangsmaskin.regler.enkelttilgang

import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import io.opentelemetry.api.trace.Span
import no.nav.tilgangsmaskin.felles.security.AuthContext
import org.springframework.stereotype.Component

@Component
class EnkeltTilgangEntityListener(private val authContext: AuthContext) {


    @PrePersist
    private fun lagrer(entity: EnkeltTilgangEntity) = setSource(entity)

    @PreUpdate
    private fun oppdaterer(entity: EnkeltTilgangEntity) = setSource(entity)

    private fun setSource(entity: EnkeltTilgangEntity) {
        entity.system = authContext.system
        entity.oppretter = authContext.ansattId?.verdi ?: authContext.system
        entity.span = Span.current().spanContext.spanId
    }
}