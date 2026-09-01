package no.nav.tilgangsmaskin.felles.rest

import jakarta.validation.ConstraintViolation
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.method.annotation.HandlerMethodValidationException
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangController
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangDevController
import org.springframework.http.ResponseEntity.status

@RestControllerAdvice(assignableTypes = [EnkeltTilgangController::class, EnkeltTilgangDevController::class])
class ValidationExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleHandlerMethodValidationException(ex: HandlerMethodValidationException) =
        handle(ex.parameterValidationResults.flatMap { result ->
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
        })

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException) =
        handle(ex.bindingResult.fieldErrors.map { error ->
            mapOf(
                "felt" to (error.field.ifBlank { "body" }),
                "melding" to (error.defaultMessage ?: "Ugyldig verdi")
            )
        })

    private fun handle(feil: List<Map<String, String>>) =
        status(BAD_REQUEST)
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
