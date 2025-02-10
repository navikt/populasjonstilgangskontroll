package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
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

    override fun ping()  {
         restClient
            .options()
            .uri(baseUri)
            .accept(APPLICATION_JSON, TEXT_PLAIN)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
    }

    fun gt(ident: String) = query<GTRespons>(graphQlClient, GT_QUERY, ident(ident))

    fun person(ident: String) = query<Person>(graphQlClient, PERSON_QUERY, ident(ident)).mapToKandidat(ident)

    fun Person.mapToKandidat(fnr: String) = PersonMapper.mapToKandidat(Fødselsnummer(fnr), this)
    override fun toString() =
        "${javaClass.simpleName} [restClient=$restClient,graphQlClient=$graphQlClient, cfg=$cfg]"

    companion object {
        private fun ident(ident: String) = mapOf(IDENT to ident)
        private const val IDENT = "ident"
        private val PERSON_QUERY = "query-person" to "hentPerson"
        private val GT_QUERY = "query-gt" to "hentGeografiskTilknytning"

    }
}

