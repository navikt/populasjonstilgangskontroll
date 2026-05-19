package no.nav.tilgangsmaskin.ansatt.graph

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGruppe(val id: UUID, val displayName: String = UTILGJENGELIG)

