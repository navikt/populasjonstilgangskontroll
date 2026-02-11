package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.rest.DefaultRestErrorHandler.Companion.IDENTIFIKATOR
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.RequestBodySpec
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.client.body
import org.springframework.web.client.requiredBody
import java.net.URI

abstract class AbstractRestClientAdapter(
        protected val restClient: RestClient,
        open val cfg: AbstractRestConfig,
        protected val errorHandler: ErrorHandler = DefaultRestErrorHandler()) : Pingable {

    protected val log = getLogger(javaClass)
    override fun ping() = if (cfg.isEnabled) get<Any>(cfg.pingEndpoint) else "disabled"

    protected inline fun <reified T : Any> get(uri: URI, headers: Map<String, String> = emptyMap()) =
        exec<T>(restClient.get().uri(uri),headers)

    protected inline fun <reified T : Any> post(uri: URI, body: Any, headers: Map<String, String> = emptyMap()) =
        exec<T>(restClient.post().uri(uri).body(body),headers)

    protected inline fun <reified T : Any> exec(spec: RequestBodySpec, headers: Map<String, String>) =
        spec
            .headers { it.setAll(headers) }
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .requiredBody<T>()

    override val name = cfg.name
    override val pingEndpoint = "${cfg.pingEndpoint}"
    override val isEnabled = cfg.isEnabled
    override fun toString() = "restClient=$restClient, cfg=$cfg, baseUri=${cfg.baseUri}"

}
