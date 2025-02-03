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
    data class EntraGruppe(val id: UUID, val displayName: String)
    companion object {
         val TOM_BOLK  = EntraGrupperBolk()
    }

}
