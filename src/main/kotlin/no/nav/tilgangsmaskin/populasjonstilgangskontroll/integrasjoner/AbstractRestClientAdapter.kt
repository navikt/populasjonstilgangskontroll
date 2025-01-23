package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner

import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.*

abstract class AbstractRestClientAdapter(
    protected val restClient: RestClient, protected val cfg: AbstractRestConfig,
    private val pingClient: RestClient = restClient
) : Pingable {

    override fun ping(): Map<String, String> {
        if (isEnabled()) {
            pingClient
                .get()
                .uri(pingEndpoint())
                .accept(APPLICATION_JSON, TEXT_PLAIN)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful) { _, _ ->
                    log.trace("Ping ${pingEndpoint()} OK")
                }
            return emptyMap()
        } else return emptyMap()
    }

    override fun name() = cfg.name
    protected val baseUri = cfg.baseUri

    override fun pingEndpoint() = "${cfg.pingEndpoint}"
    override fun isEnabled() = cfg.isEnabled
    override fun toString() = "webClient=$restClient, cfg=$cfg, pingClient=$pingClient, baseUri=$baseUri"

    companion object {
        val log = getLogger(AbstractRestClientAdapter::class.java)

        fun uri(base : URI, path : String, queryParams : HttpHeaders? = null) = builder(base, path, queryParams).build().toUri()
        private fun builder(base : URI, path : String, queryParams : HttpHeaders?) = UriComponentsBuilder.fromUri(base).pathSegment(path).queryParams(queryParams)

        private fun headerAddingRequestInterceptor(key: String, supplier: () -> String) =
            ClientHttpRequestInterceptor { req, b, next ->
                req.headers.add(key, supplier())
                next.execute(req, b)
            }
        fun behandlingRequestInterceptor() = headerAddingRequestInterceptor(BEHANDLINGSNUMMER) { BID }

        private object CallIdGenerator {
            fun create() = "${UUID.randomUUID()}"
        }

        fun correlatingRequestInterceptor(defaultConsumerId: String) =
            ClientHttpRequestInterceptor { req, b, next ->
                with(req.headers) {
                    mapOf(
                        NAV_CONSUMER_ID to consumerId(defaultConsumerId),
                        NAV_CONSUMER_ID2 to consumerId(defaultConsumerId),
                        NAV_CALL_ID to callId(),
                        NAV_CALL_ID1 to callId(),
                        NAV_CALL_ID2 to callId(),
                        NAV_CALL_ID3 to callId()
                    ).forEach { (key, value) -> add(key, value) }
                }
                next.execute(req, b)
            }

        private const val BEHANDLINGSNUMMER = "behandlingsnummer"
        private const val BID = "B897"
        private const val NAV_CONSUMER_ID = "Nav-Consumer-Id"
        private const val NAV_CONSUMER_ID2 = "consumerId"
        private const val NAV_CALL_ID = "Nav-CallId"
        private const val NAV_CALL_ID1 = "Nav-Call-Id"
        private const val NAV_CALL_ID2 = "callId"
        private const val NAV_CALL_ID3 = "X-Correlation-ID"

        private fun callId() = MDC.get(NAV_CALL_ID) ?:
            CallIdGenerator.create().also {
                toMDC(NAV_CALL_ID, it)
        }

        private fun consumerId(defaultValue: String): String = MDC.get(NAV_CONSUMER_ID) ?: run {
            toMDC(NAV_CONSUMER_ID, defaultValue)
            defaultValue
        }

        private fun toMDC(key: String, value: String?, defaultValue: String? = null) =
            MDC.put(key, value ?: defaultValue)
    }
}
interface Pingable {

    fun ping() : Map<String, String>
    fun pingEndpoint() : String
    fun name() : String = javaClass.simpleName
    fun isEnabled() : Boolean = true
}