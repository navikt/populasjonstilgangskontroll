package no.nav.tilgangsmaskin.felles.rest

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import org.apache.hc.client5.http.classic.HttpClient
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

@NoCoverageAnalysis
class HttpClientPoolMetrics(private val registry: MeterRegistry) {

    private val log = getLogger(javaClass)
    private val connectionManagers = ConcurrentHashMap.newKeySet<PoolingHttpClientConnectionManager>()
    private val warnedAboutMissingManager = AtomicBoolean(false)
    private val warnedAboutUnexpectedClient = AtomicBoolean(false)
    private val warnedAboutNonHttpComponentsFactory = AtomicBoolean(false)
    private val warnedAboutRestClientFactoryReflection = AtomicBoolean(false)

    init {
        Gauge.builder("tilgangsmaskin.http.client.pool", this) { metrics ->
            metrics.connectionManagers.size.toDouble()
        }
            .tag("state", "managers")
            .strongReference(true)
            .register(registry)
    }

    fun bind(beanName: String, restClient: RestClient) {
        val requestFactory = requestFactory(restClient)
        if (requestFactory !is HttpComponentsClientHttpRequestFactory) {
            if (warnedAboutNonHttpComponentsFactory.compareAndSet(false, true)) {
                val type = requestFactory?.javaClass?.name ?: "null"
                log.warn("Skipping HTTP pool metrics for bean {}: request factory is {}", beanName, type)
            }
            return
        }
        bind(beanName, requestFactory)
    }

    fun bind(beanName: String, factory: HttpComponentsClientHttpRequestFactory) {
        val connectionManager = connectionManager(factory.httpClient)
        if (connectionManager == null) {
            if (warnedAboutMissingManager.compareAndSet(false, true)) {
                log.warn("Unable to bind Apache HTTP client pool metrics: no pooling connection manager found")
            }
            return
        }
        if (!connectionManagers.add(connectionManager)) return

        val managerName = sanitizeManagerName(beanName)

        Gauge.builder("tilgangsmaskin.http.client.pool", connectionManager) { cm ->
            cm.totalStats.leased.toDouble()
        }
            .tag("state", "leased")
            .tag("manager", managerName)
            .strongReference(true)
            .register(registry)

        Gauge.builder("tilgangsmaskin.http.client.pool", connectionManager) { cm ->
            cm.totalStats.pending.toDouble()
        }
            .tag("state", "pending")
            .tag("manager", managerName)
            .strongReference(true)
            .register(registry)

        Gauge.builder("tilgangsmaskin.http.client.pool", connectionManager) { cm ->
            cm.totalStats.available.toDouble()
        }
            .tag("state", "available")
            .tag("manager", managerName)
            .strongReference(true)
            .register(registry)

        Gauge.builder("tilgangsmaskin.http.client.pool", connectionManager) { cm ->
            cm.totalStats.max.toDouble()
        }
            .tag("state", "max")
            .tag("manager", managerName)
            .strongReference(true)
            .register(registry)
    }

    private fun requestFactory(restClient: RestClient): ClientHttpRequestFactory? {
        return try {
            val field = restClient.javaClass.getDeclaredField("clientRequestFactory").apply {
                isAccessible = true
            }
            field.get(restClient) as? ClientHttpRequestFactory
        } catch (_: ReflectiveOperationException) {
            if (warnedAboutRestClientFactoryReflection.compareAndSet(false, true)) {
                log.warn(
                    "Unable to inspect RestClient implementation {} for request factory",
                    restClient.javaClass.name
                )
            }
            null
        }
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

    private fun sanitizeManagerName(beanName: String) =
        beanName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
}
