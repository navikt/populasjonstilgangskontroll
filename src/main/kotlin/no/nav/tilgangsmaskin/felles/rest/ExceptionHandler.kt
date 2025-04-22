package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.regler.motor.BulkRegelException
import no.nav.tilgangsmaskin.regler.motor.Metadata
import no.nav.tilgangsmaskin.regler.motor.RegelException
import org.springframework.core.annotation.Order
import org.springframework.http.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
@Order(-1)
class ExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(BulkRegelException::class)
    fun bulkExceptionHandler(e: BulkRegelException) =
        ResponseEntity.status(HttpStatus.FORBIDDEN)
            .headers(HttpHeaders().apply { contentType = MediaType.APPLICATION_PROBLEM_JSON })
            .body<ProblemDetail>(bulkDetail(e))

    fun bulkDetail(e: BulkRegelException) = ProblemDetail.forStatus(HttpStatus.FORBIDDEN).apply {
        title = e.message
        type = Metadata.Companion.TYPE_URI
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
                    "årsak" to regel.begrunnelse,
                    "brukerIdent" to brukerId.verdi
                 )
        }
}