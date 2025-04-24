package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
@Order(-1)
class ExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = getLogger(javaClass)


    @ExceptionHandler(Exception::class)
    fun recoverableFeilet(e: Exception) = if (e is RecoverableRestException) {
        e.body.apply {
            log.warn("YYYYY ${e.body}")
            setStatus(FORBIDDEN)
        }
    } else {
        log.warn("ZZZZZ Uventet feil", e)
        throw e
    }
}
