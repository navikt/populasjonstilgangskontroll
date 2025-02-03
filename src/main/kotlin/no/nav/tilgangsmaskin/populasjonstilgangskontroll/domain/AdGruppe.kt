package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID
import java.net.URI

data class AnsattSineEntraGrupper(
    @JsonProperty("@odata.context") val context: String,
    @JsonProperty("@odata.nextLink") val next: URI? = null,

    val value: List<AdGrupper> = emptyList()
)
data class AdGrupper(
    @JsonProperty("@odata.type") val odataType: String,
    val id: UUID,
    val displayName: String
)