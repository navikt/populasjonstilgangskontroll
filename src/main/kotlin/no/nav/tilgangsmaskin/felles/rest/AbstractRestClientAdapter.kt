package no.nav.tilgangsmaskin.felles.rest

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.client.body
import java.net.URI
import java.time.Duration

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
            .headers { it.setAll(headers) }
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

    /**
     * Generic helper method to fetch data from cache with standard empty check
     */
    @WithSpan
    protected inline fun <reified T> fraCache(ids: Set<String>, cache: CacheClient, cacheConfig: CachableConfig): Map<String, T> {
        if (ids.isEmpty()) {
            return emptyMap()
        }
        return cache.getMany<T>(ids, cacheConfig)
    }

    /**
     * Generic helper method to fetch data from REST with standard empty check
     */
    @WithSpan
    protected inline fun <reified T> fraRest(ids: Set<String>, restUri: URI, body: Any): Map<String, T> {
        if (ids.isEmpty()) {
            return emptyMap()
        }
        return post<Map<String, T>>(restUri, body)
    }

    /**
     * Generic helper method combining cache and REST fetch with automatic cache population
     */
    @WithSpan
    protected inline fun <reified T> hentMedCache(
        ids: Set<String>,
        cache: CacheClient,
        cacheConfig: CachableConfig,
        ttl: Duration,
        restUri: URI,
        restBody: Any
    ): Map<String, T> {
        val fraCache = fraCache<T>(ids, cache, cacheConfig)
        if (fraCache.size == ids.size) {
            return fraCache
        }

        val fraRest = fraRest<T>(ids - fraCache.keys, restUri, restBody)
        cache.putMany(fraRest, cacheConfig, ttl)
        return fraCache + fraRest
    }

}
