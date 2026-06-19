package no.nav.tilgangsmaskin.felles.rest

import io.opentelemetry.api.trace.Span
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata.Companion.TYPE_URI
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI

@RestControllerAdvice
class GlobalProblemDetailExceptionHandler(private val token: Token) : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val detail = ex.bindingResult.fieldErrors
            .joinToString(separator = ", ") { "${it.field}: ${it.defaultMessage}" }
            .ifBlank { "Validering feilet" }
        return ResponseEntity.status(status).headers(headers).body(problemDetail(status, detail, request))
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val detail = ex.mostSpecificCause.message ?: "Ugyldig eller manglende request body"
        return ResponseEntity.status(status).headers(headers).body(problemDetail(status, detail, request))
    }

    override fun handleHandlerMethodValidationException(
        ex: HandlerMethodValidationException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val detail = ex.parameterValidationResults
            .flatMap { it.resolvableErrors }
            .joinToString(separator = ", ") { it.defaultMessage ?: "Validering feilet" }
            .ifBlank { "Validering feilet" }
        return ResponseEntity.status(status).headers(headers).body(problemDetail(status, detail, request))
    }

    override fun handleErrorResponseException(
        ex: ErrorResponseException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val erStottetStatus = status == BAD_REQUEST || status == FORBIDDEN
        if (!erStottetStatus || ex !is ResponseStatusException) {
            return super.handleErrorResponseException(ex, headers, status, request)
        }
        val detail = ex.reason ?: "Ugyldig forespørsel"
        return ResponseEntity.status(status).headers(headers).body(problemDetail(status, detail, request))
    }

    private fun problemDetail(status: HttpStatusCode, detail: String, request: WebRequest) =
        ProblemDetail.forStatusAndDetail(status, detail).apply {
            title = HttpStatus.resolve(status.value())?.reasonPhrase ?: "${status.value()}"
            type = TYPE_URI
            instance = URI.create((request as ServletWebRequest).request.requestURI)
            setProperty("begrunnelse", detail)
            setProperty("traceId", Span.current().spanContext.traceId)
            token.ansattId?.let { setProperty("navIdent", it.verdi) }
        }
}






