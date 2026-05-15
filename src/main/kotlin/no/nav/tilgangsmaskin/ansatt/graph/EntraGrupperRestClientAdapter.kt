package no.nav.tilgangsmaskin.ansatt.graph

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.RestDefaultErrorHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.client.requiredBody
import java.net.URI

@Component
class EntraGrupperRestClientAdapter(
    @param:Qualifier(GRAPH) private val restClient: RestClient,
    private val cfg: EntraGrupperConfig,
    private val errorHandler: ErrorHandler = RestDefaultErrorHandler()) {


    fun grupper(ansattId: String, trengerGlobaleGrupper: Boolean) =
        generateSequence(get<EntraGrupper>(cfg.grupperURI(ansattId, trengerGlobaleGrupper))) { bolk ->
            bolk.next?.let {
                get<EntraGrupper>(it)
            }
        }.flatMapTo(mutableSetOf()) { it.value }.toSet()

    private inline fun <reified T : Any> get(uri: URI, headers: Map<String, String> = emptyMap()) =
        restClient.get()
            .uri(uri)
            .accept(APPLICATION_JSON)
            .headers { it.setAll(headers) }
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .requiredBody<T>()


    @Generated
    override fun toString() = "${javaClass.simpleName} [client=$restClient, config=$cfg, errorHandler=$errorHandler]"

}

@JsonIgnoreProperties(ignoreUnknown = true)
private data class EntraGrupper(@param:JsonProperty("@odata.nextLink") val next: URI? = null, val value: Set<EntraGruppe> = emptySet())
