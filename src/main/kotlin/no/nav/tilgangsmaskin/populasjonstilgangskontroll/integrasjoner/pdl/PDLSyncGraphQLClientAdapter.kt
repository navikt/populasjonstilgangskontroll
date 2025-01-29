package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractGraphQLAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.GraphQLErrorHandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLConfig.Companion.PDL
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.graphql.client.GraphQlClient
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Component
class PDLGraphQLClientAdapter(@Qualifier(PDL) private val graphQlClient: GraphQlClient,
                              @Qualifier(PDL) restClient: RestClient,
                              graphQlErrorHandler: GraphQLErrorHandler,
                              errorHandler: ErrorHandler,
                              cfg: PDLConfig) : AbstractGraphQLAdapter(restClient, cfg,errorHandler,graphQlErrorHandler) {

    override fun ping(): Map<String, String> {
        restClient
            .options()
            .uri(baseUri)
            .accept(APPLICATION_JSON, TEXT_PLAIN)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .onStatus(HttpStatusCode::is2xxSuccessful, ::successHandler)
            .toBodilessEntity()
        return emptyMap()
    }

    fun person(ident: String) = query<Person>(graphQlClient, PERSON_QUERY, mapOf(IDENT to ident))

    override fun toString() =
        "${javaClass.simpleName} [restClient=$restClient,graphQlClient=$graphQlClient, cfg=$cfg]"

    companion object {
        private val IDENT = "ident"
        private val PERSON_QUERY = "query-person" to "hentPerson"
    }
}

