package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGrupperBolk(
    @JsonProperty("@odata.nextLink") val next: URI? = null,
    val value: List<EntraGruppe> = emptyList())   {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class EntraGruppe(val id: UUID, val gruppeNavn: String)
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class MSGraphSaksbehandlerResponse(@JsonProperty("value") val attributter: List<MSGraphSaksbehandlerAttributter>)  {
    data class MSGraphSaksbehandlerAttributter(
        val id: UUID,
        val onPremisesSamAccountName: NavId,
        val displayName: String,
        val givenName: String,
        val surname: String,
        val streetAddress: String
    )
}
