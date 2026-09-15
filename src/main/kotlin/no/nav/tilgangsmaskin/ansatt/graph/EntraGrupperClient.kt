package no.nav.tilgangsmaskin.ansatt.graph

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.RestDefaultErrorHandler.Companion.IDENTIFIKATOR
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.GetExchange
import java.net.URI

@ClientRegistrationId(GRAPH)
interface EntraGrupperClient {
    @GetExchange
    fun grupper(uri: URI,@RequestHeader(IDENTIFIKATOR) identifikator: String): EntraGrupper
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGrupper(
    @param:JsonProperty("@odata.nextLink") val next: URI? = null,
    val value: Set<EntraGruppe> = emptySet()
)
