package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.MSGraphConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.client.RestClient
import java.util.UUID
import org.springframework.web.client.body
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component


@Component
class  MSRestClientAdapter(@Qualifier(GRAPH) restClient: RestClient, private val cf: MSGraphConfig
):AbstractRestClientAdapter(restClient,cf) {

    fun hentUUIDforNavIdent(navIdent: String) :Any {
        return restClient.get()
            .uri(navIdent, cf::azureUriUser )
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::is2xxSuccessful) { _, _ ->
                log.trace("MS Graph ${pingEndpoint()} OK")
            }.body<Any>() ?: throw RuntimeException("Klarte ikke å hente UUID for navIdent") //

    }


    fun hentGrupperForNavIdent(ansattId: UUID): List<AdGruppe> {

        return restClient.post()
            .uri(uri(baseUri, "/users/$ansattId/getMemberGroups"))
            .accept(APPLICATION_JSON)
            .body(HentAdGrupperForNavAnsatt.Request(true))
            .retrieve()
            .onStatus(HttpStatusCode::is2xxSuccessful) { _, _ ->
                log.trace("MS Graph ${pingEndpoint()} OK")
            }.body<HentAdGrupperForNavAnsatt.Response>()?.value?.map { (id,name) -> AdGruppe(id, name) } ?: emptyList() //TODO bygge støtte for paginering siden det er kun 100 grupper som returneres
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

object HentUUIDForNavIdent {
    data class Request(
        val securityEnabledOnly: Boolean
    )
    data class Response(val value: UUID) {
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


