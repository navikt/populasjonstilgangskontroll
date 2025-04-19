package no.nav.tilgangsmaskin.ansatt.entra

import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.HEADER_CONSISTENCY_LEVEL
import no.nav.tilgangsmaskin.felles.AbstractPingableHealthIndicator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient

@Configuration
class EntraClientBeanConfig {

    @Bean
    @Qualifier(GRAPH)
    fun graphRestClient(b: RestClient.Builder, cfg: EntraConfig) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptors {
                it.add(headerAddingRequestInterceptor(HEADER_CONSISTENCY_LEVEL))
            }.build()


    @Bean
    fun graphHealthIndicator(a: EntraRestClientAdapter) = object : AbstractPingableHealthIndicator(a) {}

    private fun headerAddingRequestInterceptor(vararg verdier: Pair<String, String>) =
        ClientHttpRequestInterceptor { req, b, next ->
            verdier.forEach { req.headers.add(it.first, it.second) }
            next.execute(req, b)
        }
}