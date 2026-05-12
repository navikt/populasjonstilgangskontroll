package no.nav.tilgangsmaskin.felles.rest

import org.springframework.http.HttpStatusCode
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.client.support.RestClientAdapter.create
import org.springframework.web.service.invoker.HttpServiceProxyFactory.builderFor

object RestClientFactory {
    fun createProxyFactory(cfg: AbstractRestConfig, builder: Builder, errorHandler: ErrorHandler) =
        builderFor(
            create(
                builder.baseUrl(cfg.baseUri)
                    .defaultStatusHandler(HttpStatusCode::isError, errorHandler::handle)
                    .build()
            )
        ).build()

    inline fun <reified T : Any> createClient(cfg: AbstractRestConfig, builder: Builder, errorHandler: ErrorHandler =RestDefaultErrorHandler()) = createProxyFactory(cfg, builder, errorHandler).createClient(T::class.java)
}

