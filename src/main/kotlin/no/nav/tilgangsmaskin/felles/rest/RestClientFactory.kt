package no.nav.tilgangsmaskin.felles.rest

import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.support.RestClientAdapter.create
import org.springframework.web.service.invoker.HttpServiceProxyFactory.builderFor
import java.net.URI

object RestClientFactory {
    fun proxyFactory(baseUri: URI, builder: Builder, vararg interceptors: ClientHttpRequestInterceptor) =
        builderFor(create(restClient(baseUri, builder, *interceptors))).build()

    inline fun <reified T : Any> createClient(cfg: RestConfig, builder: Builder, vararg interceptors: ClientHttpRequestInterceptor) =
        proxyFactory(cfg.baseUri, builder, *interceptors).createClient(T::class.java)

    private fun restClient(baseUri: URI, builder: Builder, vararg interceptors: ClientHttpRequestInterceptor) =
        builder
            .baseUrl(baseUri)
            .requestInterceptors { it.addAll(interceptors) }
            .build()
}
