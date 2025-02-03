package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGrupper(
    @JsonProperty("@odata.nextLink") val next: URI? = null,
    val value: List<EntraGruppeInfo> = emptyList())   {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class EntraGruppeInfo(val id: UUID, val displayName: String)
}
