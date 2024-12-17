package no.nav.tilgangsmaskin.populasjonstilgangskontroll.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class PdlClientConfiguration(private val webClientBuilder: WebClient.Builder) {

    @Value("\${pdl_base.url}")
    private lateinit var pdlUrl: String

    @Value("\${SERVICE_USER_USERNAME}")
    private lateinit var username: String


    @Bean
    fun pdlWebClient(): WebClient {
        return webClientBuilder
            .baseUrl(pdlUrl)
            .defaultHeader("Nav-Consumer-Id", username)
            .build()
    }
}