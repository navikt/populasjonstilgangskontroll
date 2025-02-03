package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattSineEntraGrupper
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.MSGraphConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.client.RestClient
import java.util.UUID
import org.springframework.web.client.body
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Component
class  MSRestClientAdapter(@Qualifier(GRAPH) restClient: RestClient, private val cf: MSGraphConfig, errorHandler: ErrorHandler): AbstractRestClientAdapter(
    restClient,cf, errorHandler) {
    fun hentUUIDforNavIdent(ident: String) =
        restClient.get()
            .uri { cf.userURI(it, ident) }
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::is2xxSuccessful, ::successHandler)
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .body<Any>() ?: throw RuntimeException("Klarte ikke å hente UUID for navIdent $ident") //

    fun hentGrupperForNavIdent(ansattId: UUID) =
        restClient.get()
            .uri { cf.grupperURI(it,ansattId) }
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::is2xxSuccessful, ::successHandler)
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .body<Any>() ?: AnsattSineEntraGrupper("no ctx")
}

private data class Request(val securityEnabledOnly: Boolean = true)
