package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

abstract class AbstractRestClientAdapter(
    protected val restClient: RestClient,
    protected val cfg: AbstractRestConfig,
    protected val errorHandler: ErrorHandler,
    private val pingClient: RestClient = restClient,
) : Pingable {

    protected val log = getLogger(AbstractRestClientAdapter::class.java)

    protected fun successHandler(req: HttpRequest, res: ClientHttpResponse ) =
        log.trace("Oppslag mot {} response {}", req.uri, res.statusCode)

    override fun ping(): Map<String, String> {
        pingClient
            .get()
            .uri(pingEndpoint())
            .accept(APPLICATION_JSON)
            .exchange { req, res ->
                if (res.statusCode == HttpStatus.OK) {
                    successHandler(req, res)
                } else {
                    errorHandler.handle(req, res)
                }
            }
        return emptyMap<String,String>().also {
            log.trace("PING mot {} OK", pingEndpoint())
        }
    }

    override fun name() = cfg.name
    protected val baseUri = cfg.baseUri

    override fun pingEndpoint() = "${cfg.pingEndpoint}"
    override fun isEnabled() = cfg.isEnabled
    override fun toString() = "webClient=$restClient, cfg=$cfg, pingClient=$pingClient, baseUri=$baseUri"

    companion object {

        fun uri(base : URI, path : String, queryParams : HttpHeaders? = null) = builder(base, path, queryParams).build().toUri()
        private fun builder(base : URI, path : String, queryParams : HttpHeaders?) = UriComponentsBuilder.fromUri(base).pathSegment(path).queryParams(queryParams)

        fun headerAddingRequestInterceptor(vararg verdier : Pair<String, String>) =
            ClientHttpRequestInterceptor { req, b, next ->
                verdier.forEach { (key, value) -> req.headers.add(key, value) }
                next.execute(req, b)
            }

    }
}
