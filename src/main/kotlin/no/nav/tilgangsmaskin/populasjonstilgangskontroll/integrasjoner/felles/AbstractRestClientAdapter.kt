package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.DefaultRestErrorHandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.IrrecoverableRestException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import java.net.URI

abstract class AbstractRestClientAdapter(
    protected val restClient: RestClient,
    protected val cfg: AbstractRestConfig,
    protected val errorHandler: ErrorHandler = DefaultRestErrorHandler()
) : Pingable {

    protected val log = getLogger(AbstractRestClientAdapter::class.java)
    override fun ping() = get<Unit>(cfg.pingEndpoint)

     protected inline fun <reified T> get(uri: URI, headers: Map<String, String> = emptyMap()) =
        restClient.get()
            .uri(uri)
            .accept(APPLICATION_JSON)
            .headers { it.setAll(headers) }
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .body(T::class.java) ?: throw IrrecoverableRestException(INTERNAL_SERVER_ERROR, uri)

    protected inline fun <reified T> post(uri: URI, body: Any,headers: Map<String, String> = emptyMap()) =
        restClient
            .post()
            .uri(uri)
            .headers { it.setAll(headers) }
            .accept(APPLICATION_JSON)
            .body(body)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .body(T::class.java) ?: throw IrrecoverableRestException(INTERNAL_SERVER_ERROR, uri)

    override fun name() = cfg.name
    protected val baseUri = cfg.baseUri

    override fun pingEndpoint() = "${cfg.pingEndpoint}"
    override fun isEnabled() = cfg.isEnabled
    override fun toString() = "webClient=$restClient, cfg=$cfg, baseUri=$baseUri"

    companion object {

        fun headerAddingRequestInterceptor(vararg verdier : Pair<String, String>) =
            ClientHttpRequestInterceptor { req, b, next ->
                verdier.forEach { (key, value) -> req.headers.add(key, value) }
                next.execute(req, b)
            }
    }
}
