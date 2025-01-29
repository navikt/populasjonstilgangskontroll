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
class  MSRestClientAdapter(@Qualifier(GRAPH) restClient: RestClient, private val cf: MSGraphConfig): AbstractRestClientAdapter(restClient,cf) {

    fun hentUUIDforNavIdent(ident: String) = restClient.get()
        .uri {
            cf.azureUriUser(it, ident)
        }
        .accept(APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatusCode::is2xxSuccessful) { _, _ ->
            log.trace("MS Graph uuid oppslag OK")
        }.body<Any>() ?: throw RuntimeException("Klarte ikke å hente UUID for navIdent $ident") //

    fun hentGrupperForNavIdent(ansattId: UUID) =
        restClient.post()
            .uri(uri(baseUri, "/users/$ansattId/getMemberGroups"))
            .accept(APPLICATION_JSON)
            .body(HentAdGrupperForNavAnsatt.Request(true))
            .retrieve()
            .onStatus(HttpStatusCode::is2xxSuccessful) { _, _ ->
                log.trace("MS Graph grupper OK")
            }.body<List<AdGruppe>>() ?: emptyList()
}


object HentAdGrupperForNavAnsatt {

    data class Request(
        val securityEnabledOnly: Boolean
    )

}


