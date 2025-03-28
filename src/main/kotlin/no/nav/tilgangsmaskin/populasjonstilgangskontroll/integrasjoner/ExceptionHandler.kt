package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.BulkRegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelBeskrivelse.Companion.TYPE_URI
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ProblemDetail
import org.springframework.http.ProblemDetail.forStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
@Order(-1)
class ExceptionHandler : ResponseEntityExceptionHandler() {

    protected val log = getLogger(ExceptionHandler::class.java)


    @ExceptionHandler(BulkRegelException::class)
    fun bulkExceptionHandler(e: BulkRegelException) =
        status(FORBIDDEN)
            .headers(HttpHeaders().apply { contentType = APPLICATION_PROBLEM_JSON })
            .body<ProblemDetail>(bulkDetail(e))

    override fun handleHttpMessageNotWritable(
        ex: HttpMessageNotWritableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest): ResponseEntity<in Any>? {
        log.error("Feil ved skriving av melding", ex)
        return super.handleHttpMessageNotWritable(ex, headers, status, request)
    }

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
                "brukerIdent" to brukerId.verdi)
        }
}
