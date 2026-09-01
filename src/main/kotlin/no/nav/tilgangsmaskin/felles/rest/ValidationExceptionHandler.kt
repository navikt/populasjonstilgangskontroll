package no.nav.tilgangsmaskin.felles.rest

import jakarta.validation.ConstraintViolation
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.core.annotation.Order
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangController
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangDevController
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE

//@RestControllerAdvice(basePackageClasses = [EnkeltTilgangController::class, EnkeltTilgangDevController::class])
//@Order(HIGHEST_PRECEDENCE)
class ValidationExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleHandlerMethodValidationException(
        ex: HandlerMethodValidationException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val feil = ex.parameterValidationResults.flatMap { result ->
            result.resolvableErrors.map { error ->
                val violation = runCatching {
                    result.unwrap(error, ConstraintViolation::class.java)
                }.getOrNull()

                val field = when {
                    violation != null -> violation.propertyPath.lastOrNull()?.toString() ?: "body"
                    else -> "body"
                }

                mapOf(
                    "felt" to field,
                    "melding" to (violation?.message ?: error.defaultMessage ?: "Ugyldig verdi")
                )
            }
        }

        return ResponseEntity.status(BAD_REQUEST)
            .contentType(APPLICATION_PROBLEM_JSON)
            .body(
                mapOf(
                    "title" to "Validering feilet",
                    "status" to BAD_REQUEST.value(),
                    "detail" to "En eller flere felter er ugyldige",
                    "feil" to feil
                )
            )
    }
}
