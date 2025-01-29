package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class AdGruppe(
    @JsonProperty("@odata.context") val context: String,
                    val value: List<String>
)
