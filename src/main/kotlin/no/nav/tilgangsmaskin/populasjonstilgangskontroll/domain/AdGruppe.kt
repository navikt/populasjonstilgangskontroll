package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class AnsattSineEntraGrupper(
    @JsonProperty("@odata.context") val context: String,
    val value: List<AdGrupper> = emptyList()
)
data class AdGrupper(
    @JsonProperty("@odata.type") val odataType: String,
    val id: UUID,
    val displayName: String
)