package no.nav.tilgangsmaskin.felles.rest

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangController
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangDevController

@RestControllerAdvice(assignableTypes = [EnkeltTilgangController::class, EnkeltTilgangDevController::class])
class ValidationExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleHandlerMethodValidationException(ex: HandlerMethodValidationException): ResponseEntity<Any> {
        return ResponseEntity.status(BAD_REQUEST)
            .contentType(APPLICATION_PROBLEM_JSON)
            .body(
                mapOf(
                    "title" to "Validering feilet",
                    "status" to BAD_REQUEST.value(),
                    "detail" to "En eller flere felter er ugyldige",
                    "feil" to ex.parameterValidationResults.flatMap { result ->
                        result.resolvableErrors.map { error ->
                            mapOf(
                                "felt" to "body",
                                "melding" to (error.defaultMessage ?: "Ugyldig verdi")
                            )
                        }
                    }
                )
            )
    }
}
