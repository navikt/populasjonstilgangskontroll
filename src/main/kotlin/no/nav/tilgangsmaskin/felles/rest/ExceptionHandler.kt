package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(-1)
class ExceptionHandler {

    private val log = getLogger(javaClass)


    @ExceptionHandler(RecoverableRestException::class)
    fun recoverableFeilet(e: RecoverableRestException) =
        e.body.apply {
            log.warn("YYYYY ${e.body}")
            setStatus(FORBIDDEN)
        }
}
