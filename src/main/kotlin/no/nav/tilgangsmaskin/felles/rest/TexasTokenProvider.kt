package no.nav.tilgangsmaskin.felles.rest

import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.ObservationRegistry.NOOP
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class TexasTokenProvider(
    @Value("\${texas.token-endpoint}") endpoint: String,
    observationRegistry: ObjectProvider<ObservationRegistry>,
) {
    private val texasClient = RestClient.builder()
        .baseUrl(endpoint)
        .observationRegistry(observationRegistry.getIfAvailable { NOOP })
        .build()

    fun interceptorFor(scope: String) = TexasTokenInterceptor(texasClient, scope)
}
