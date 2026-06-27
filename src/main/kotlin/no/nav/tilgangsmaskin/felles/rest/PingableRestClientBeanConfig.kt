package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient.Builder

abstract class PingableRestClientBeanConfig(
    protected val cfg: RestConfig,
) {
    protected inline fun <reified T : Any> createClient(
        builder: Builder,
        vararg interceptors: ClientHttpRequestInterceptor) =
        RestClientFactory.createClient<T>(cfg, builder, RestDefaultErrorHandler(), *interceptors)

    protected fun healthIndicator(ping: () -> Any?) =
        PingableHealthIndicator(cfg, ping)
}


