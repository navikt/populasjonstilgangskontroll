package no.nav.tilgangsmaskin.felles.rest

import org.springframework.http.HttpStatusCode
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.client.support.RestClientAdapter.create
import org.springframework.web.service.invoker.HttpServiceProxyFactory.builderFor
import java.net.URI

object RestClientFactory {
    fun createProxyFactory(baseUri: URI, builder: Builder, errorHandler: ErrorHandler) =
        builderFor(
            create(
                builder.baseUrl(baseUri)
                    .defaultStatusHandler(HttpStatusCode::isError, errorHandler::handle)
                    .build()
            )
        ).build()

    inline fun <reified T : Any> createClient(cfg: RestConfig, builder: Builder, errorHandler: ErrorHandler = RestDefaultErrorHandler()) =
        createProxyFactory(cfg.baseUri, builder, errorHandler).createClient(T::class.java)
}

