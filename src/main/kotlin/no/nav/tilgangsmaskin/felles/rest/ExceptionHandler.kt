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

    protected val log = getLogger(javaClass)


    @ExceptionHandler(RuntimeException::class)
    fun recoverableFeilet(e: RuntimeException) = if (e is RecoverableRestException) {
        e.body.apply {
            log.warn("XXXXXX ${e.body}")
            setStatus(FORBIDDEN)
        }
    } else {
        log.warn("Uventet feil", e)
        throw e
    }
}
