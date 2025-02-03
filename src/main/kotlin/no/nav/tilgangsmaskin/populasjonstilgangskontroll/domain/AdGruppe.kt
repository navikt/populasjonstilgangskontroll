package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI
import java.util.*

data class EntraGrupper(
    @JsonProperty("@odata.context") val context: String,
    @JsonProperty("@odata.nextLink") val next: URI? = null,
    val value: List<EntraGruppeInfo> = emptyList()
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGruppeInfo(
  //  @JsonProperty("@odata.type") val odataType: String,
    val id: UUID,
    val displayName: String
)