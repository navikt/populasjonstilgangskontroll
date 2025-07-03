package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail.forStatusAndDetail
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import java.net.URI

@Component
@Primary
class DefaultRestErrorHandler : ErrorHandler {
    private val log = getLogger(javaClass)

    override fun handle(req: HttpRequest, res: ClientHttpResponse) {
        if (res.statusCode.is4xxClientError) throw IrrecoverableRestException(res.statusCode, req.uri, res.statusText).also {
            log.warn("Irrecoverable exception etter ${res.statusCode.value()} fra ${req.uri}")
        }
        else throw RecoverableRestException(res.statusCode, req.uri, res.statusText).also {
            log.warn("Recoverable exception etter ${res.statusCode.value()} fra ${req.uri}")
        }
    }
}

open class IrrecoverableRestException(
        status: HttpStatusCode, uri: URI, msg: String = (status as HttpStatus).reasonPhrase,
        cause: Throwable? = null) : ErrorResponseException(status, problemDetail(status, msg, uri), cause)

open class RecoverableRestException(
        status: HttpStatusCode,
        uri: URI,
        msg: String = (status as HttpStatus).reasonPhrase,
        cause: Throwable? = null) : ErrorResponseException(status, problemDetail(status, msg, uri), cause)

private fun problemDetail(status: HttpStatusCode, msg: String, uri: URI) =
    forStatusAndDetail(status, msg).apply {
        title = "${status.value()}"
        properties = mapOf("uri" to "$uri")
    }

@ControllerAdvice
class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): Nothing {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
        throw ErrorResponseException(HttpStatus.BAD_REQUEST,forStatusAndDetail(HttpStatus.BAD_REQUEST,"").apply {
            title = "Valideringsfeil"
            properties = mapOf("errors" to errors)
        },e).also {
            log.warn("Valideringsfeil: ${errors.entries.joinToString(", ")}")
        }
    }
}
