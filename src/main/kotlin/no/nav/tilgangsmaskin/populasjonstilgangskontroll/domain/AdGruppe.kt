package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class AdGruppeIder(
    @JsonProperty("@odata.context") val context: String,
                    val value: List<String>
)
