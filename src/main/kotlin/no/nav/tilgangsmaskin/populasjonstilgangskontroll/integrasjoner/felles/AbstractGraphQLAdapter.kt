package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import org.springframework.graphql.client.GraphQlClient
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

abstract class AbstractGraphQLAdapter(client: RestClient, cfg: AbstractRestConfig, errorHandler: ErrorHandler,protected val graphQlErrorHandler: GraphQLErrorHandler) : AbstractRestClientAdapter(client, cfg, errorHandler) {

    protected inline fun <reified T> query(graphQL: GraphQlClient, query: Pair<String, String>, vars: Map<String, String>) =
        runCatching {
            graphQL
                .documentName(query.first)
                .variables(vars)
                .executeSync()
                .field(query.second)
                .toEntity(T::class.java) ?: throw IllegalStateException("Fant ikke feltet ${query.second} i responsen") // TODO bedre exception
        }.getOrElse {
            log.warn("Feil ved oppslag av {}", T::class.java.simpleName, it)
            graphQlErrorHandler.handle(cfg.baseUri, it)
        }
}

