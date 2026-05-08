package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.DefaultRestErrorHandler
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.client.requiredBody
import java.net.URI

@Component
class EntraRestClientAdapter(
    @param:Qualifier(GRAPH) private val restClient: RestClient,
    private val entraClient: EntraGraphClient,
    val cfg: EntraConfig,
    private val errorHandler: ErrorHandler = DefaultRestErrorHandler()) {

    private val log = getLogger(javaClass)

    fun oidFraEntra(ansattId: String) =
         with(entraClient.findUser(filter = "onPremisesSamAccountName eq '$ansattId'").oids) {
             log.info("Fant $size oids i Entra for $ansattId")
            when (size) {
                0 -> throw EntraOidException(ansattId, "Fant ingen oid for navident $ansattId, er den fremdeles gyldig?")
                1 -> single().id
                else -> throw EntraOidException(ansattId, "Forventet nøyaktig én oid for navident $ansattId, fant $size (${joinToString(", ") { it.id.toString() }})")
            }
    }

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