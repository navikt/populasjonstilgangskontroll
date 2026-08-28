package no.nav.tilgangsmaskin.felles.rest

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class, BindException::class)
    fun handleBinding(ex: Exception): ResponseEntity<Map<String, Any>> {
        val bindingResult = when (ex) {
            is MethodArgumentNotValidException -> ex.bindingResult
            is BindException -> ex.bindingResult
            else -> throw IllegalStateException("Unsupported binding exception: ${ex::class}", ex)
        }

        return badRequest(
            bindingResult.fieldErrors.map { error ->
                mapOf(
                    "felt" to error.field,
                    "melding" to (error.defaultMessage ?: "Ugyldig verdi")
                )
            } + bindingResult.globalErrors.map { error ->
                mapOf(
                    "felt" to error.objectName,
                    "melding" to (error.defaultMessage ?: "Ugyldig verdi")
                )
            }
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<Map<String, Any>> = badRequest(
        ex.constraintViolations.map { violation ->
            mapOf(
                "felt" to violation.propertyPath.toString(),
                "melding" to (violation.message ?: "Ugyldig verdi")
            )
        }
    )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableMessage(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, Any>> = badRequest(
        listOf(
            mapOf(
                "felt" to "body",
                "melding" to (ex.mostSpecificCause?.message ?: "Request body er ugyldig")
            )
        )
    )

    @ExceptionHandler(ErrorResponseException::class)
    fun handleErrorResponse(ex: ErrorResponseException): ResponseEntity<Map<String, Any>> {
        val problemDetail = ex.body
        if (problemDetail.status == BAD_REQUEST.value() && problemDetail.title == "Bad Request") {
            return badRequest(
                listOf(
                    mapOf(
                        "felt" to "body",
                        "melding" to (problemDetail.detail ?: "Validation failure")
                    )
                )
            )
        }

        throw ex
    }

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
