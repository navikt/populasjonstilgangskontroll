package no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors

import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
@Order(-1)
class ExceptionHandler : ResponseEntityExceptionHandler() {
    private val log = getLogger(javaClass)

    @ExceptionHandler(Exception::class)
    fun catchAll(e: Exception, req: NativeWebRequest) = createProblem(e, req, BAD_REQUEST)

    private fun createProblem(e: Exception, req: NativeWebRequest, status: HttpStatus) =
        status(status)
            .headers(HttpHeaders().apply { contentType = APPLICATION_PROBLEM_JSON })
            .body(createProblemDetail(e, status, e.message ?: e.javaClass.simpleName, null, null, req).apply {
            }.also {
                log.error("OOPS $req $it ${status.reasonPhrase}: ${e.message}", e)
            })
}
