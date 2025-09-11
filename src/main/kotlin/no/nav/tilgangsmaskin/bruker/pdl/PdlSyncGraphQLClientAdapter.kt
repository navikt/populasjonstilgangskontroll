package no.nav.tilgangsmaskin.bruker.pdl

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPartner
import no.nav.tilgangsmaskin.felles.graphql.AbstractSyncGraphQLAdapter
import no.nav.tilgangsmaskin.felles.graphql.GraphQLErrorHandler
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.graphql.client.GraphQlClient
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Component
@Timed( value = "pdl_tjeneste", histogram = true, extraTags = ["backend", "graphql"] )
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

    fun partnere(ident: String) =
        runCatching {
            query<Partnere>(SIVILSTAND_QUERY, ident(ident)).sivilstand.mapNotNull {
                it.relatertVedSivilstand?.let { brukerId ->
                    Familie.FamilieMedlem(BrukerId(brukerId), tilPartner(it.type))
                }
            }.toSet()
        }.getOrElse {
            if (it is IrrecoverableRestException && it.statusCode == NOT_FOUND) {
                log.trace("Fant ingen partnere for $ident")
                return emptySet<Familie.FamilieMedlem>()
            }
            else throw it
        }


    override fun toString() =
        "${javaClass.simpleName} [restClient=$restClient,graphQlClient=$graphQlClient, cfg=$cfg]"

    companion object {
        private const val IDENT = "ident"
        private fun ident(ident: String) = mapOf(IDENT to ident)
        private val SIVILSTAND_QUERY = "query-sivilstand" to "hentPerson"
    }
}

