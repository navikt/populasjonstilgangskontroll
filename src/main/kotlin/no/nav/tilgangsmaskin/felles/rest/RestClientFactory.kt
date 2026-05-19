package no.nav.tilgangsmaskin.felles.rest

import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.client.support.RestClientAdapter.create
import org.springframework.web.service.invoker.HttpServiceProxyFactory.builderFor
import java.net.URI

object RestClientFactory {
    fun createProxyFactory(baseUri: URI,
                           builder: Builder,
                           errorHandler: ErrorHandler,
                           vararg interceptors: ClientHttpRequestInterceptor) =
        builderFor(
            create(
                builder.baseUrl(baseUri)
                    .requestInterceptors {
                        it.addAll(interceptors)
                    }
                    .defaultStatusHandler(HttpStatusCode::isError, errorHandler::handle)
                    .build()
            )
        ).build()

    inline fun <reified T : Any> createClient(cfg: RestConfig,
                                              builder: Builder,
                                              errorHandler: ErrorHandler = RestDefaultErrorHandler(),
                                              vararg interceptors: ClientHttpRequestInterceptor) =
        createProxyFactory(cfg.baseUri, builder, errorHandler, *interceptors).createClient(T::class.java)
}

