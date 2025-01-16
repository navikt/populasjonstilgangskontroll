package no.nav.tilgangsmaskin.populasjonstilgangskontroll.auth

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result


class TokenClient(
    private val config: AzureConfig
) {
    private val objectMapper = jacksonObjectMapper()


    fun getToken(formData: List<Pair<String, String>>): String {


        val newToken = fetchNewToken(formData)
        return newToken.access_token
    }

    private fun fetchNewToken(formData: List<Pair<String, String>>): TokenResponse {
        val (_, response, result) = Fuel.post(config.tokenEndpoint)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(formData.joinToString("&") { "${it.first}=${it.second}" })
            .response()

        return when (result) {
            is Result.Success -> {
                val responseBody = response.body().asString("application/json")
                objectMapper.readValue(responseBody, TokenResponse::class.java)
            }
            is Result.Failure -> {
               // log.error("Feil ved henting av token: ", result.getException()) //TODI logge error
                throw RuntimeException("Feil ved henting av token", result.getException())
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TokenResponse(
        val access_token: String,
        val expires_in: Int,
    )
}
