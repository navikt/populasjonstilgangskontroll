package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.RecoverableException
import org.slf4j.LoggerFactory
import org.springframework.graphql.ResponseError
import org.springframework.graphql.client.FieldAccessException
import org.springframework.graphql.client.GraphQlTransportException
import org.springframework.http.HttpStatus
import java.net.URI
import java.util.Locale
import kotlin.jvm.javaClass

interface GraphQLErrorHandler {
    fun handle(uri: URI, e: Throwable): Nothing =
        when (e) {
            is FieldAccessException -> throw e.oversett(uri)
            is GraphQlTransportException -> throw RecoverableException(HttpStatus.INTERNAL_SERVER_ERROR,
                uri, e.message ?: "Transport feil", e)
            else -> throw IrrecoverableException(HttpStatus.INTERNAL_SERVER_ERROR, uri, e.message, cause = e)
        }

    companion object {
        private val log = LoggerFactory.getLogger(GraphQLErrorHandler::class.java)
        fun FieldAccessException.oversett(uri: URI) = response.errors.oversett(message, uri)

        private fun List<ResponseError>.oversett(message: String?, uri: URI) = oversett(
            firstOrNull()?.extensions?.get("code")?.toString() ?: HttpStatus.INTERNAL_SERVER_ERROR.name,
            message ?: "Ukjent feil", uri)
            .also {
                log.warn("GraphQL oppslag returnerte $size feil, oversatte $message til ${it.javaClass.simpleName}", this)
            }

        fun oversett(kode: String, msg: String, uri: URI) = IrrecoverableException(kode.tilStatus(), uri, msg)
        private fun String.tilStatus() = HttpStatus.valueOf(this.uppercase(Locale.getDefault()))

    }
}