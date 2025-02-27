package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableRestException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.RecoverableRestException
import org.slf4j.LoggerFactory
import org.springframework.graphql.ResponseError
import org.springframework.graphql.client.FieldAccessException
import org.springframework.graphql.client.GraphQlTransportException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import java.net.URI
import java.util.*

interface GraphQLErrorHandler {
    fun handle(uri: URI, e: Throwable): Nothing =
        when (e) {
            is FieldAccessException ->  throw e.oversett(uri)
            is GraphQlTransportException ->  throw RecoverableRestException(INTERNAL_SERVER_ERROR,
                uri,
                e.message ?: "Uventet respons",
                e)
            else ->  throw IrrecoverableRestException(INTERNAL_SERVER_ERROR, uri, e.message?: "Uventet respons", e)
        }

    companion object {
        private val log = LoggerFactory.getLogger(GraphQLErrorHandler::class.java)
        private fun FieldAccessException.oversett(uri: URI) = response.errors.oversett(message, uri)

        private fun List<ResponseError>.oversett(message: String?, uri: URI) = oversett(
            firstOrNull()?.extensions?.get("code")?.toString() ?: INTERNAL_SERVER_ERROR.name,
            message ?: "Ukjent feil", uri)
            .also {
                log.warn("GraphQL oppslag returnerte $size feil, oversatte $message til ${it.javaClass.simpleName}", this)
            }

        private fun oversett(kode: String, msg: String, uri: URI) = IrrecoverableRestException(kode.tilStatus(), uri, msg)
        private fun String.tilStatus() =  if (this.uppercase() == "UNAUTHENTICATED") HttpStatus.UNAUTHORIZED else  HttpStatus.valueOf(this.uppercase(Locale.getDefault()))

    }
}