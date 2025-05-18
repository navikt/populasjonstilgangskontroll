package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.regler.motor.RegelException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(-1)
class ExceptionHandler {

    private val log = getLogger(javaClass)


   // @ExceptionHandler(RegelException::class)
   // fun  regelException(e: RegelException) : Nothing = throw e.also { log.info("Enriching and rethrowing") }
}
