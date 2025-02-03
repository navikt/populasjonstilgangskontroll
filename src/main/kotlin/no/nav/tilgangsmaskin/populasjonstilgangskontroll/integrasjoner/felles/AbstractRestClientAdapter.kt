package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpHeaders
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
    protected val errorHandler: ErrorHandler,
    private val pingClient: RestClient = restClient,
) : Pingable {

    protected val log = getLogger(AbstractRestClientAdapter::class.java)
    override fun ping(): Map<String, String> = get<Map<String, String>>(cfg.pingEndpoint,pingClient)

    protected inline fun <reified T> get(uri: URI,client: RestClient = restClient) =
        client.get()
            .uri(uri)
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .body(T::class.java) ?: throw IrrecoverableException(INTERNAL_SERVER_ERROR, uri)


    override fun name() = cfg.name
    protected val baseUri = cfg.baseUri

    override fun pingEndpoint() = "${cfg.pingEndpoint}"
    override fun isEnabled() = cfg.isEnabled
    override fun toString() = "webClient=$restClient, cfg=$cfg, pingClient=$pingClient, baseUri=$baseUri"

    companion object {

        fun headerAddingRequestInterceptor(vararg verdier : Pair<String, String>) =
            ClientHttpRequestInterceptor { req, b, next ->
                verdier.forEach { (key, value) -> req.headers.add(key, value) }
                next.execute(req, b)
            }

    }
}
