package no.nav.tilgangsmaskin.felles.rest

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val detail = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
            .ifBlank { ex.message }
        return ResponseEntity.status(status).body(problemDetail(status, detail, request))
    }

    override fun handleErrorResponseException(
        ex: ErrorResponseException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        if (ex !is ResponseStatusException) return super.handleErrorResponseException(ex, headers, status, request)
        val detail = ex.reason ?: (status as? HttpStatus)?.reasonPhrase ?: status.toString()
        return ResponseEntity.status(status).body(problemDetail(status, detail, request))
    }

    private fun problemDetail(status: HttpStatusCode, detail: String, request: WebRequest) =
        ProblemDetail.forStatusAndDetail(status, detail).apply {
            title = "${status.value()}"
            instance = URI.create((request as ServletWebRequest).request.requestURI)
        }
}


