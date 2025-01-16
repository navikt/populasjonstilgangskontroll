package no.nav.tilgangsmaskin.populasjonstilgangskontroll.auth

import no.nav.security.token.support.core.configuration.IssuerProperties
import org.springframework.beans.factory.annotation.Value
import java.net.URL

data class AzureConfig (
    val azureClientSecret: String,
    val azureClientId: String,
    val tokenEndpoint: String
) {
    val azureConfig = AzureConfig(
        System.getenv("AZURE_APP_CLIENT_SECRET"),
        System.getenv("AZURE_APP_CLIENT_ID"),
        System.getenv("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT")
    )

    val azureIssuerProperties = IssuerProperties(
        URL(System.getenv("AZURE_APP_WELL_KNOWN_URL")),
        listOf(System.getenv("AZURE_APP_CLIENT_ID")),
        System.getenv("AZURE_OPENID_CONFIG_ISSUER")
    )
}