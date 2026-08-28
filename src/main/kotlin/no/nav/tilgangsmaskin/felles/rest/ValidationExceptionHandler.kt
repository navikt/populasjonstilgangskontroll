package no.nav.tilgangsmaskin.felles.rest

import jakarta.validation.ConstraintViolation
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException

@RestControllerAdvice
class ValidationExceptionHandler {



    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleValidation(ex: HandlerMethodValidationException): ResponseEntity<Map<String, Any>> = badRequest(
        ex.parameterValidationResults.flatMap { result ->
            result.resolvableErrors.map { error ->
                val violation = runCatching {
                    result.unwrap(error, ConstraintViolation::class.java)
                }.getOrNull()

                val field = when {
                    violation != null -> violation.propertyPath.toString()
                    else -> "body"
                }

                mapOf(
                    "felt" to field,
                    "melding" to (violation?.message ?: error.defaultMessage ?: "Ugyldig verdi")
                )
            }
        }
    )

    @ExceptionHandler(ErrorResponseException::class)
    fun handleErrorResponse(ex: ErrorResponseException): ResponseEntity<Any> =
        ResponseEntity.status(ex.statusCode)
            .contentType(APPLICATION_PROBLEM_JSON)
            .body(ex.body)

    private fun badRequest(feil: List<Map<String, String>>): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(BAD_REQUEST)
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
