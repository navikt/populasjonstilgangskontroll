
package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import java.util.UUID
import java.net.URI
import com.fasterxml.jackson.annotation.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.EntraGruppe

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGrupperBolk(
    @JsonProperty("@odata.nextLink") val next: URI? = null,
    val value: List<EntraGruppe> = emptyList())   {
}