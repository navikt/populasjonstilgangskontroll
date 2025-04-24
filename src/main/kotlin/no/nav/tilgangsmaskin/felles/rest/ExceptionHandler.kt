package no.nav.tilgangsmaskin.felles.rest

import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
@Order(-1)
class ExceptionHandler : ResponseEntityExceptionHandler()

@ExceptionHandler(RecoverableRestException::class)
fun recoverableFeilet(e: RecoverableRestException) =
    e.body.apply {
        setStatus(FORBIDDEN)
    }