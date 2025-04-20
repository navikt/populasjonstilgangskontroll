package no.nav.tilgangsmaskin.felles

import org.springframework.graphql.client.GraphQlClient
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

abstract class AbstractSyncGraphQLAdapter(
        protected val graphQlClient: GraphQlClient,
        client: RestClient,
        cf: AbstractRestConfig,
        errorHandler: ErrorHandler,
        protected val graphQlErrorHandler: GraphQLErrorHandler) : AbstractRestClientAdapter(client, cf, errorHandler) {

    protected inline fun <reified T> query(query: Pair<String, String>, vars: Map<String, String>) =
        runCatching {
            graphQlClient
                .documentName(query.first)
                .variables(vars)
                .executeSync()
                .field(query.second)
                .toEntity(T::class.java) ?: throw IrrecoverableRestException(
                    INTERNAL_SERVER_ERROR, cfg.baseUri, "Fant ikke feltet ${query.second} i responsen")
        }.getOrElse {
            log.warn("Feil ved oppslag av {}", T::class.java.simpleName, it)
            graphQlErrorHandler.handle(cfg.baseUri, it)
        }
}

