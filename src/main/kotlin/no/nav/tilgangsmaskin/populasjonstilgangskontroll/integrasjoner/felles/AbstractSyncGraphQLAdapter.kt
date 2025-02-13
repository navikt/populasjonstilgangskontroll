package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import org.springframework.graphql.client.GraphQlClient
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

abstract class AbstractSyncGraphQLAdapter(client: RestClient, cfg: AbstractRestConfig, errorHandler: ErrorHandler, protected val graphQlErrorHandler: GraphQLErrorHandler) : AbstractRestClientAdapter(client, cfg, errorHandler) {

    protected inline fun <reified T> query(graphQL: GraphQlClient, query: Pair<String, String>, vars: Map<String, String>) =
        runCatching {
            graphQL
                .documentName(query.first)
                .variables(vars)
                .executeSync()
                .field(query.second)
                .toEntity(T::class.java) ?: throw IrrecoverableException(INTERNAL_SERVER_ERROR,"Fant ikke feltet ${query.second} i responsen")
        }.getOrElse {
            log.warn("Feil ved oppslag av {}", T::class.java.simpleName, it)
            graphQlErrorHandler.handle(cfg.baseUri, it)
        }
}

