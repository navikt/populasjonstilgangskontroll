package no.nav.tilgangsmaskin.felles.rest

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import org.apache.hc.client5.http.classic.HttpClient
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.core5.pool.PoolStats
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

@NoCoverageAnalysis
class HttpClientPoolMetrics(registry: MeterRegistry) {

    private val log = getLogger(javaClass)
    private val connectionManagers = ConcurrentHashMap.newKeySet<PoolingHttpClientConnectionManager>()
    private val warnedAboutMissingManager = AtomicBoolean(false)
    private val warnedAboutUnexpectedClient = AtomicBoolean(false)

    init {
        listOf("leased", "pending", "available", "max").forEach { state ->
            Gauge.builder("tilgangsmaskin.http.client.pool", this) { metrics ->
                metrics.totalStats().valueFor(state).toDouble()
            }
                .tag("state", state)
                .strongReference(true)
                .register(registry)
        }
        Gauge.builder("tilgangsmaskin.http.client.pool", this) { metrics ->
            metrics.connectionManagers.size.toDouble()
        }
            .tag("state", "managers")
            .strongReference(true)
            .register(registry)
    }

    fun bind(factory: HttpComponentsClientHttpRequestFactory) {
        val connectionManager = connectionManager(factory.httpClient)
        if (connectionManager == null) {
            if (warnedAboutMissingManager.compareAndSet(false, true)) {
                log.warn("Unable to bind Apache HTTP client pool metrics: no pooling connection manager found")
            }
            return
        }
        connectionManagers.add(connectionManager)
    }

    private fun connectionManager(httpClient: HttpClient): PoolingHttpClientConnectionManager? {
        if (httpClient.javaClass.name != "org.apache.hc.client5.http.impl.classic.InternalHttpClient") {
            if (warnedAboutUnexpectedClient.compareAndSet(false, true)) {
                log.warn(
                    "Unable to bind Apache HTTP client pool metrics: unsupported client type {}",
                    httpClient.javaClass.name
                )
            }
            return null
        }
        val field = httpClient.javaClass.getDeclaredField("connManager").apply {
            isAccessible = true
        }
        return field.get(httpClient) as? PoolingHttpClientConnectionManager
    }

    private fun totalStats(): PoolStats {
        var leased = 0
        var pending = 0
        var available = 0
        var max = 0
        connectionManagers.forEach { manager ->
            val stats = manager.getTotalStats()
            leased += stats.leased
            pending += stats.pending
            available += stats.available
            max += stats.max
        }
        return PoolStats(
            leased,
            pending,
            available,
            max,
        )
    }

    private fun PoolStats.valueFor(state: String): Int =
        when (state) {
            "leased" -> leased
            "pending" -> pending
            "available" -> available
            "max" -> max
            else -> 0
        }
}
