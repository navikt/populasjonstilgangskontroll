package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import tools.jackson.databind.json.JsonMapper
import java.util.Base64

class TexasShadowInterceptor(private val texasClient: RestClient, private val scope: String) :
    ClientHttpRequestInterceptor {

    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution) =
        execution.execute(request, body).also {
            runCatching { compare(request) }
                .onFailure { log.warn("Texas shadow feilet for scope={}: {}", scope, it.message) }
        }

    private fun compare(request: HttpRequest) {
        val oauth2Token = request.headers.getFirst("Authorization")?.removePrefix("Bearer ") ?: return
        val texasToken = fetchToken()

        val oauth2Claims = decodePayload(oauth2Token)
        val texasClaims = decodePayload(texasToken)

        val mismatches = COMPARABLE_CLAIMS
            .filter { oauth2Claims[it] != texasClaims[it] }
            .map { "$it=[oauth2=${oauth2Claims[it]}, texas=${texasClaims[it]}]" }

        if (mismatches.isEmpty()) {
            log.info("Texas shadow MATCH scope={} path={}", scope, request.uri.path)
        } else {
            log.warn("Texas shadow MISMATCH scope={} path={} diff={}", scope, request.uri.path, mismatches)
        }
    }

    private fun fetchToken(): String =
        texasClient.post()
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(LinkedMultiValueMap<String, String>().apply {
                add("identity_provider", "azuread")
                add("target", scope)
            })
            .retrieve()
            .body<TokenResponse>()!!
            .access_token

    @Suppress("UNCHECKED_CAST")
    private fun decodePayload(token: String): Map<String, Any?> {
        val raw = token.split(".").getOrNull(1) ?: return emptyMap()
        val padded = raw.padEnd((raw.length + 3) / 4 * 4, '=')
        val bytes = Base64.getUrlDecoder().decode(padded)
        return mapper.readValue(bytes, Map::class.java) as Map<String, Any?>
    }

    private data class TokenResponse(val access_token: String)

    companion object {
        private val log = LoggerFactory.getLogger(TexasShadowInterceptor::class.java)
        private val mapper = JsonMapper()
        private val COMPARABLE_CLAIMS = setOf("iss", "aud", "appid", "azp", "idtyp", "roles", "scp")
    }
}
