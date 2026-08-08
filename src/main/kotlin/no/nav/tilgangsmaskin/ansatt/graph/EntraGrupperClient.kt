package no.nav.tilgangsmaskin.ansatt.graph

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRAPH
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId
import org.springframework.web.service.annotation.GetExchange
import java.net.URI

@ClientRegistrationId(GRAPH)
interface EntraGrupperClient {
    @GetExchange
    fun grupper(uri: URI): EntraGrupper
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGrupper(
    @param:JsonProperty("@odata.nextLink") val next: URI? = null,
    val value: Set<EntraGruppe> = emptySet()
)
