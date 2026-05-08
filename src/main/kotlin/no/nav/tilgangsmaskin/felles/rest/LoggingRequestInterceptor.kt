package no.nav.tilgangsmaskin.felles.rest

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component

@Component
class LoggingRequestInterceptor : ClientHttpRequestInterceptor {
    private val log = getLogger(javaClass)
    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        if (request.uri.path.contains("monitoring")) {
            return execution.execute(request, body)
        }
        if (!body.isEmpty()) {
            log.debug(CONFIDENTIAL, "Body for {} {} : {} ",request.method, request.uri,String(body))
        }
        val response = execution.execute(request, body)
        if (!response.statusCode.is2xxSuccessful) {
            log.debug(CONFIDENTIAL,"Response status for {} {}: {}", request.method, request.uri, response.statusCode)
        }
        return response
    }
}