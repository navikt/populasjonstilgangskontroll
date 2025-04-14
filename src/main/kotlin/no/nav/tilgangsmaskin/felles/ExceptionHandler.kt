package no.nav.tilgangsmaskin.felles

import no.nav.tilgangsmaskin.regler.motor.BulkRegelException
import no.nav.tilgangsmaskin.regler.motor.RegelBeskrivelse.Companion.TYPE_URI
import no.nav.tilgangsmaskin.regler.motor.RegelException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ProblemDetail
import org.springframework.http.ProblemDetail.forStatus
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

    fun bulkDetail(e: BulkRegelException) = forStatus(FORBIDDEN).apply {
        title = e.message
        type = TYPE_URI
        properties = mapOf(
            "navIdent" to e.ansattId.verdi,
            "avvisninger" to e.exceptions.size,
            "begrunnelser" to e.exceptions.map { props(it) }
        )
    }

    private fun props(e: RegelException) =
        with(e) {
            mapOf(
                "kode" to kode,
                "årsak" to regel.avvisningTekst,
                "brukerIdent" to brukerId.verdi
            )
        }
}
