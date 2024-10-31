package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.MicrosoftGraphClientImpl
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.testutils.MockHttpServer
import no.nav.security.mock.*

class MicrosoftGraphClientTest {
    companion object {
        private val mockServer = MockHttpServer()

        @BeforeAll
        @JvmStatic
        fun start() {
            mockServer.start()
        }
    }

    @AfterEach
    fun reset() {
        mockServer.reset()
    }

    @Test
    fun `hentAdGrupperForNavAnsatt - skal lage riktig request og parse respons`() {
        val client = MicrosoftGraphClientImpl(
            baseUrl = mockServer.serverUrl(),
            tokenProvider = { "TOKEN" },
        )

        val navAnsattAzureId = UUID.randomUUID()
        val adGroupAzureId = UUID.randomUUID()

        mockServer.handleRequest(
            response = MockResponse()
                .setBody(
                    """
						{
							"@odata.context": "https://graph.microsoft.com/v1.0/${"$"}metadata#Collection(Edm.String)",
							"value": [
								"$adGroupAzureId"
							]
						}
					""".trimIndent()
                )
        )

        val adGrupper = client.hentAdGrupperForNavAnsatt(navAnsattAzureId)

        adGrupper.first().equals(adGroupAzureId)

        val request = mockServer.latestRequest()

        request.path.equals("/v1.0/users/$navAnsattAzureId/getMemberGroups")
        request.method.equals("POST")
        request.getHeader("Authorization").equals("Bearer TOKEN")

        val expectedRequestJson = """
			{"securityEnabledOnly":true}
		""".trimIndent()

        request.body.readUtf8().equals(expectedRequestJson)
    }


}