
package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import java.util.UUID
import com.fasterxml.jackson.annotation.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGrupperBolk(
    @JsonProperty("@odata.nextLink") val next: java.net.URI? = null,
    val value: List<EntraGruppe> = emptyList())   {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class EntraGruppe(val id: UUID, val displayName: String)
}