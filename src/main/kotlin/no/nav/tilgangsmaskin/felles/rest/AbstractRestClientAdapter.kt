package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.rest.DefaultRestErrorHandler.Companion.IDENTIFIKATOR
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.client.body
import java.net.URI

abstract class AbstractRestClientAdapter(
        protected val restClient: RestClient,
        val cfg: AbstractRestConfig,
        protected val errorHandler: ErrorHandler = DefaultRestErrorHandler()) : Pingable {

    protected val log = getLogger(javaClass)
    override fun ping() = if (cfg.isEnabled) get<Any>(cfg.pingEndpoint) else "disabled"

    protected inline fun <reified T : Any> get(uri: URI, headers: Map<String, String> = emptyMap()) =
        restClient.get()
            .uri(uri)
            .accept(APPLICATION_JSON)
            .headers { it.setAll(headers.filterKeys { key -> key != IDENTIFIKATOR }) }
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .body<T>() ?: throw IrrecoverableRestException(INTERNAL_SERVER_ERROR, uri)

    protected inline fun <reified T : Any> post(uri: URI, body: Any, headers: Map<String, String> = emptyMap()) =
        restClient
            .post()
            .uri(uri)
            .headers { it.setAll(headers) }
            .accept(APPLICATION_JSON)
            .body(body)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .body<T>() ?: throw IrrecoverableRestException(INTERNAL_SERVER_ERROR, uri)

    override val name = cfg.name

    override val pingEndpoint = "${cfg.pingEndpoint}"
    override val isEnabled = cfg.isEnabled
    override fun toString() = "restClient=$restClient, cfg=$cfg, baseUri=${cfg.baseUri}"

}
