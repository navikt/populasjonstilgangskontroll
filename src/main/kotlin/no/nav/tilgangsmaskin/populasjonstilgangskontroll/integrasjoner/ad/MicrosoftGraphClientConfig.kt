package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.auth.TokenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
open class MicrosoftGraphClientConfig {

    @Value("\${microsoft.graph.url}")
    lateinit var baseUrl: String

    @Value("\${microsoft.graph.scope}")
    lateinit var scope: String


    open fun microsoftGraphClient(tokenClient: TokenClient): MicrosoftGraphClient {
        return MicrosoftGraphClientImpl(
            baseUrl = baseUrl,
            tokenProvider = { tokenClient.getToken(
                formData = TODO()
            ) },
        )
    }

}