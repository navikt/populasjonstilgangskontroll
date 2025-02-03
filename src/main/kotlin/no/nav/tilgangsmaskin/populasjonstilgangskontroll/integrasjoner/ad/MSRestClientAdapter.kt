package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.EntraGrupperBolk
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.EntraGrupperBolk.EntraGruppe
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
import java.net.URI

@Component
class  MSRestClientAdapter(@Qualifier(GRAPH) restClient: RestClient, private val cf: MSGraphConfig, errorHandler: ErrorHandler): AbstractRestClientAdapter(
    restClient,cf, errorHandler) {
    fun hentUUIDforNavIdent(ident: String) =
        restClient.get()
            .uri { cf.userURI(it, ident) }
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .body<Any>() ?: throw RuntimeException("Klarte ikke å hente UUID for navIdent $ident") //

    fun grupperForNavIdent(ansattId: UUID): List<EntraGruppe> {
        var bolk = restClient.get()
            .uri { cf.grupperURI(it,ansattId) }
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .body<EntraGrupperBolk>() ?: EntraGrupperBolk()
             val grupper = bolk.value.toMutableList()
             log.info("Grupper for ansatt $ansattId: $grupper")
             while (bolk.next != null) {
                 bolk = neste(bolk.next)
                 grupper += bolk.value
                 log.info("${grupper.size} page for ansatt $ansattId: $grupper")
             }
             return grupper
    }

    private fun neste(uri:URI): EntraGrupperBolk {
        return restClient.get()
            .uri(uri)
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .body<EntraGrupperBolk>() ?: EntraGrupperBolk()
    }
}