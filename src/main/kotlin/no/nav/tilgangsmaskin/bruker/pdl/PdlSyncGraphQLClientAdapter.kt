package no.nav.tilgangsmaskin.bruker.pdl

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPartner
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.graphql.GraphQLErrorHandler
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.Pingable
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.graphql.client.GraphQlClient
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Component
@Timed(value = "pdl_tjeneste", histogram = true, extraTags = ["backend", "graphql"])
class PdlSyncGraphQLClientAdapter(
    @param:Qualifier(PDLGRAPH) private val graphQlClient: GraphQlClient,
    @param:Qualifier(PDLGRAPH) private val restClient: RestClient,
    private val graphQlErrorHandler: GraphQLErrorHandler,
    private val cfg: PdlGraphQLConfig,
    private val errorHandler: ErrorHandler) : Pingable {

    private val log = getLogger(javaClass)

    override val name = cfg.name
    override val pingEndpoint = cfg.pingEndpoint

    override fun ping() {
        restClient
            .options()
            .uri(cfg.baseUri)
            .accept(APPLICATION_JSON, TEXT_PLAIN)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorHandler::handle)
            .toBodilessEntity()
    }

    fun partnere(ident: String): Set<FamilieMedlem> =
        runCatching {
            query<Partnere>(SIVILSTAND_QUERY, ident(ident)).sivilstand.mapNotNull {
                it.relatertVedSivilstand?.let { brukerId ->
                    FamilieMedlem(BrukerId(brukerId), tilPartner(it.type))
                }
            }.toSet()
        }.getOrElse {
            if (it is IrrecoverableRestException && it.statusCode == NOT_FOUND) {
                log.trace("Fant ingen partnere for $ident")
                return emptySet()
            } else throw it
        }

    private inline fun <reified T : Any> query(query: Pair<String, String>, vars: Map<String, String>) =
        runCatching {
            graphQlClient
                .documentName(query.first)
                .variables(vars)
                .executeSync()
                .field(query.second)
                .toEntity(T::class.java) ?: throw IrrecoverableRestException(INTERNAL_SERVER_ERROR, cfg.baseUri, "Fant ikke feltet ${query.second} i responsen")
        }.getOrElse {
            log.warn("Feil ved oppslag av {}", T::class.java.simpleName, it)
            graphQlErrorHandler.handle(cfg.baseUri, it)
        }

    @Generated
    override fun toString() =
        "${javaClass.simpleName} [restClient=$restClient, graphQlClient=$graphQlClient, cfg=$cfg]"

    companion object {
        private const val IDENT = "ident"
        private fun ident(ident: String) = mapOf(IDENT to ident)
        private val SIVILSTAND_QUERY = "query-sivilstand" to "hentPerson"
    }
}
