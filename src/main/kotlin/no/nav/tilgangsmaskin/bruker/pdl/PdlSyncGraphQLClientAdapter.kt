package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.felles.graphql.AbstractSyncGraphQLAdapter
import no.nav.tilgangsmaskin.felles.graphql.GraphQLErrorHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.graphql.client.GraphQlClient
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Component
class PdlSyncGraphQLClientAdapter(
        @Qualifier(PDLGRAPH) graphQlClient: GraphQlClient,
        @Qualifier(PDLGRAPH) restClient: RestClient,
        graphQlErrorHandler: GraphQLErrorHandler,
        cfg: PdlGraphQLConfig,
        errorHandler: ErrorHandler) :
    AbstractSyncGraphQLAdapter(graphQlClient, restClient, cfg, errorHandler, graphQlErrorHandler) {

    override fun ping() {
        restClient
            .options()
            .uri(cfg.baseUri)
            .accept(APPLICATION_JSON, TEXT_PLAIN)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
    }

    fun sivilstand(ident: String) = query<Partnere>(SIVILSTAND_QUERY, ident(ident))

    override fun toString() =
        "${javaClass.simpleName} [restClient=$restClient,graphQlClient=$graphQlClient, cfg=$cfg]"

    companion object {
        private const val IDENT = "ident"
        private fun ident(ident: String) = mapOf(IDENT to ident)
        private val SIVILSTAND_QUERY = "query-sivilstand" to "hentPerson"
    }
}

