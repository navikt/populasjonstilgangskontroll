package no.nav.tilgangsmaskin.felles.rest

import io.micrometer.observation.ObservationRegistry
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class TexasShadowProvider(
    @Value("\${texas.token-endpoint}") endpoint: String,
    observationRegistry: ObjectProvider<ObservationRegistry>,
) {
    private val texasClient = RestClient.builder()
        .baseUrl(endpoint)
        .observationRegistry(observationRegistry.getIfAvailable { ObservationRegistry.NOOP })
        .build()

    fun interceptorFor(scope: String): ClientHttpRequestInterceptor = TexasShadowInterceptor(scope, texasClient)
}
