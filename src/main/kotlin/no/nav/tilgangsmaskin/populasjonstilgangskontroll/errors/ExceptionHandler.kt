package no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BulkRegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.Companion.TYPE_URI
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ProblemDetail
import org.springframework.http.ProblemDetail.*
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
@Order(-1)
class ExceptionHandler : ResponseEntityExceptionHandler() {



    @ExceptionHandler(BulkRegelException::class)
    fun bulkExceptionHandler(e: BulkRegelException) =
        status(FORBIDDEN)
            .headers(HttpHeaders().apply { contentType = APPLICATION_PROBLEM_JSON })
            .body<ProblemDetail>(bulkDetail(e))

    @ExceptionHandler(Throwable::class)
    fun catchAll(e: Throwable) =   logger.error(e.message, e) }

    fun bulkDetail(e: BulkRegelException) = forStatus(FORBIDDEN).apply {
        title = e.message
        type = TYPE_URI
        properties = mapOf(
            "navIdent" to e.ansattId.verdi,
            "avvisninger" to e.exceptions.size,
            "detaljer" to e.exceptions.map { ::props }
        )
    }

    fun props(e: RegelException) =
        with(e) {
            mapOf(
                "årsak" to regel.metadata.begrunnelse,
                "brukerIdent" to brukerId.verdi,
                "kanOverstyres" to regel.erOverstyrbar)
        }
}
