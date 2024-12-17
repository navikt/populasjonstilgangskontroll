package no.nav.tilgangsmaskin.populasjonstilgangskontroll.config

import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableJwtTokenValidation
open class ApplicationConfig {

    @Bean
    open fun machineToMachineTokenClient(
        @Value("\${nais.env.azureAppClientId}") azureAdClientId: String,
        @Value("\${nais.env.azureOpenIdConfigTokenEndpoint}") azureTokenEndpoint: String,
        @Value("\${nais.env.azureAppJWK}") azureAdJWK: String
    ): MachineToMachineTokenClient {
        return AzureAdTokenClientBuilder.builder()
            .withClientId(azureAdClientId)
            .withTokenEndpointUrl(azureTokenEndpoint)
            .withPrivateJwk(azureAdJWK)
            .buildMachineToMachineTokenClient()
    }
}