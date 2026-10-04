package no.nav.tilgangsmaskin.ansatt.graph

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.sikkerhetstjenesten.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGruppe(val id: UUID, val displayName: String = UTILGJENGELIG)

