package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.AzureObjectId
import okhttp3.OkHttpClient
import no.nav.common.rest.client.RestClient.baseClient
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.JsonUtils.fromJsonString
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.JsonUtils.toJsonString
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.RestUtils.authorization
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.RestUtils.toJsonRequestBody
import okhttp3.Request

class  MicrosoftGraphClientImpl(
    private val baseUrl: String,
    private val tokenProvider: () -> String,
    private val client: OkHttpClient = baseClient()
):MicrosoftGraphClient {

    @Timed("microsoft_graph.hent_nav_ident_med_azure_id", histogram = true, percentiles = [0.5, 0.95, 0.99], extraTags = ["type", "client"])
    override fun hentAdGrupperForNavAnsatt(navAnsattAzureId: AzureObjectId): List<AdGruppe> {
        val requestData = HentAdGrupperForNavAnsatt.Request(true)
        val request = Request.Builder()
            .url("$baseUrl/v1.0/users/$navAnsattAzureId/getMemberGroups")
            .post(toJsonString(requestData).toJsonRequestBody()) // sett opp spørring med å flitere vekk alle grupper som ikkje har 0000-GA- i navnet
            .authorization(tokenProvider)
            .build()

        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("Klarte ikke å hente AdGrupper for navAnsatt")
            }
            val body = response.body?.string() ?: throw RuntimeException("Body is missing")
            val responseData = fromJsonString<HentAdGrupperForNavAnsatt.Response>(body)
            responseData.value.map { AdGruppe(it.id, it.name) }




    }

}
object HentAdGrupperForNavAnsatt {

    data class Request(
        val securityEnabledOnly: Boolean
    )

    data class Response(
        val value: List<AdGruppe>
    ) }}


