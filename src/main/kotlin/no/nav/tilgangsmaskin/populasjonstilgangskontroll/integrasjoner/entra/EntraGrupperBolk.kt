
package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGrupperBolk(@JsonProperty("@odata.nextLink") val next: URI? = null, val value: List<EntraGruppe> = emptyList())

