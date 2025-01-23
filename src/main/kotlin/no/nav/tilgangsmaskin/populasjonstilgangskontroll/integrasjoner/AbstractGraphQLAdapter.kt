package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.RecoverableException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.graphql.ResponseError
import org.springframework.graphql.client.ClientGraphQlRequest
import org.springframework.graphql.client.FieldAccessException
import org.springframework.graphql.client.GraphQlClient
import org.springframework.graphql.client.GraphQlTransportException
import org.springframework.graphql.client.SyncGraphQlClientInterceptor
import org.springframework.graphql.client.SyncGraphQlClientInterceptor.Chain
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.web.client.RestClient
import java.net.URI
import java.util.Locale.getDefault

abstract class AbstractGraphQLAdapter(client: RestClient, cfg: AbstractRestConfig, protected val errorHandler: GraphQLErrorHandler = object : GraphQLErrorHandler {}) : AbstractRestClientAdapter(client, cfg) {

    protected inline fun <reified T> query(graphQL: GraphQlClient, query: Pair<String, String>, vars: Map<String, String>) =
        runCatching {
            graphQL
                .documentName(query.first)
                .variables(vars)
                .executeSync()
                .field(query.second)
                .toEntity(T::class.java)
        }.getOrElse {
            log.warn("Feil ved oppslag av {}", T::class.java.simpleName, it)
            errorHandler.handle(cfg.baseUri, it)
        }
}

interface GraphQLErrorHandler {
    fun handle(uri: URI, e: Throwable): Nothing =
        when (e) {
            is FieldAccessException -> throw e.oversett(uri)
            is GraphQlTransportException -> throw RecoverableException(INTERNAL_SERVER_ERROR, uri, e.message ?: "Transport feil", e)
            else -> throw IrrecoverableException(INTERNAL_SERVER_ERROR, uri, e.message, cause = e)
        }

    companion object {
        val LOG = getLogger(GraphQLErrorHandler::class.java)
        fun FieldAccessException.oversett(uri: URI) = response.errors.oversett(message, uri)

        private fun List<ResponseError>.oversett(message: String?, uri: URI) = oversett(
            firstOrNull()?.extensions?.get("code")?.toString() ?: INTERNAL_SERVER_ERROR.name,
            message ?: "Ukjent feil",
            uri)
            .also {
                LOG.warn("GraphQL oppslag returnerte $size feil, oversatte $message til ${it.javaClass.simpleName}",
                    this)
            }

        fun oversett(kode: String, msg: String, uri: URI) = IrrecoverableException(kode.tilStatus(), uri, msg)
        private fun String.tilStatus() = HttpStatus.valueOf(this.uppercase(getDefault()))

    }
}

class LoggingGraphQLInterceptor : SyncGraphQlClientInterceptor {

    private val log = getLogger(LoggingGraphQLInterceptor::class.java)

    override fun intercept(req: ClientGraphQlRequest, chain: Chain) =
        chain.next(req).also {
            log.trace("Eksekverer {} med variabler {}", req.document, req.variables)
        }
}

