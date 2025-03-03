package no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BulkRegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
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

    fun bulkDetail(e: BulkRegelException) = ProblemDetail.forStatus(FORBIDDEN).apply {
        title = e.message
        properties = mapOf(
            "navIdent" to e.ansattId.verdi,
            "avvisninger" to e.exceptions.size,
            "detaljer" to e.exceptions.map { properties(it) }
        )
    }

    fun properties(e: RegelException) = mapOf(
        "årsak" to e.regel.metadata.begrunnelse,
        "brukerIdent" to e.brukerId.verdi,
        "kanOverstyres" to e.regel.erOverstyrbar)
}


