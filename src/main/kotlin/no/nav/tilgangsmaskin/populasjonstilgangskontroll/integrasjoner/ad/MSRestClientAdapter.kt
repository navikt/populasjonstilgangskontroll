package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.client.RestClient
import java.util.UUID
import org.springframework.web.client.body
import org.springframework.http.HttpStatusCode



class  MSRestClientAdapter(restClient: RestClient, private val cf: MSGraphConfig
):AbstractRestClientAdapter(restClient,cf) {

     fun hentAdGrupperForNavAnsatt(ansattId: UUID): List<AdGruppe> {

        return restClient.post()
            .uri(uri(baseUri, "/v1.0/users/$ansattId/getMemberGroups"))
            .accept(APPLICATION_JSON)
            .body(HentAdGrupperForNavAnsatt.Request(true))
            .retrieve()
            .onStatus(HttpStatusCode::is2xxSuccessful) { _, _ ->
                log.trace("MS Graph ${pingEndpoint()} OK")
            }.body<HentAdGrupperForNavAnsatt.Response>()?.value?.map { (id,name) -> AdGruppe(id, name) } ?: emptyList()
         /*
        val requestData = HentAdGrupperForNavAnsatt.Request(true)
        val request = Request.Builder()
            .url("$baseUrl/v1.0/users/$navAnsattAzureId/getMemberGroups")
            .post(toJsonString(requestData).toJsonRequestBody()) // TODO sett opp spørring med å flitere vekk alle grupper som ikkje har 0000-GA- i navnet
            .authorization(tokenProvider)
            .build() //TODO bygge støtte for paginering

        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("Klarte ikke å hente AdGrupper for navAnsatt")
            }
            val body = response.body?.string() ?: throw RuntimeException("Body is missing")
            val responseData = fromJsonString<HentAdGrupperForNavAnsatt.Response>(body)
            responseData.value.map { AdGruppe(it.id, it.name) }

          */




    }

}
object HentAdGrupperForNavAnsatt {

    data class Request(
        val securityEnabledOnly: Boolean
    )

    data class Response(val value: List<AdGruppe>) {
        data class AdGruppe(
            val id: UUID,
            val name: String
        )
    }
}


