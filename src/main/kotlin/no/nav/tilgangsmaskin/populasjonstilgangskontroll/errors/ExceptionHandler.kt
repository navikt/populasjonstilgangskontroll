package no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BulkRegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
@Order(-1)
class ExceptionHandler : ResponseEntityExceptionHandler() {
    private val log = getLogger(javaClass)

    @ExceptionHandler(BulkRegelException::class)
    fun bulk(e: BulkRegelException, req: NativeWebRequest) = problem(e, req)

    private fun problem(e: BulkRegelException, req: NativeWebRequest) : ProblemDetail{
        return ProblemDetail.forStatus(FORBIDDEN).apply {
            title  = e.body.title
            properties = mapOf(
                "navIdent" to e.ansattId.verdi,
                "avvisninger" to e.exceptions.size,
                "detaljer" to e.exceptions.map {properties(it)}
            )
        }
        status(FORBIDDEN)
            .headers(HttpHeaders().apply { contentType = APPLICATION_PROBLEM_JSON })
            .body(problem(e, req).apply {
            })
    }
}
fun properties(e: RegelException) : Map<String, Any> {
    return mapOf(
        "kode" to e.regel.metadata.begrunnelse,
        "brukerIdent" to e.brukerId.verdi,
        "kanOverstyres" to e.regel.erOverstyrbar)
}