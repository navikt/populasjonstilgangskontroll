package no.nav.tilgangsmaskin.ansatt.entra

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGruppe(val id: UUID, val displayName: String = UTILGJENGELIG) {
    @Generated
    override fun equals(other: Any?) = other is EntraGruppe && id == other.id

    @Generated
    override fun hashCode() = id.hashCode()
}

