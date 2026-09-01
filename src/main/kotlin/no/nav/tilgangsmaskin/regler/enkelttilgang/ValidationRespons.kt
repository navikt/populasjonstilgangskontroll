package no.nav.tilgangsmaskin.regler.enkelttilgang

import jakarta.validation.ConstraintViolation
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.web.method.annotation.HandlerMethodValidationException

fun valideringsfeilRespons(ex: HandlerMethodValidationException): ResponseEntity<Any> =
    status(BAD_REQUEST)
        .contentType(APPLICATION_PROBLEM_JSON)
        .body(
            mapOf(
                "title" to "Validering feilet",
                "status" to BAD_REQUEST.value(),
                "detail" to "En eller flere felter er ugyldige",
                "feil" to ex.parameterValidationResults.flatMap { result ->
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
            )
        )
