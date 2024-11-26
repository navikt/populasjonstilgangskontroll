package no.nav.tilgangsmaskin.populasjonstilgangskontroll.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class PdlClientConfiguration(private val webClientBuilder: WebClient.Builder) {

    @Value("\${PDL_BASE_URL}")
    private lateinit var pdlUrl: String

    @Value("\${SERVICE_USER_USERNAME}")
    private lateinit var username: String

    @Value("\${PDL_APIKEY}")
    private lateinit var apiKey: String

    @Bean
    fun pdlWebClient(): WebClient {
        return webClientBuilder
            .baseUrl(pdlUrl)
            .defaultHeader("Nav-Consumer-Id", username)
            .defaultHeader("x-nav-apiKey", apiKey)
            .build()
    }
}