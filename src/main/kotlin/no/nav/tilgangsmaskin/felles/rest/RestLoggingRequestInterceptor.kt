package no.nav.tilgangsmaskin.felles.rest

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class RestLoggingRequestInterceptor : ClientHttpRequestInterceptor {
    private val log = getLogger(javaClass)
    override fun intercept(req: HttpRequest,
                           body: ByteArray,
                           execution: ClientHttpRequestExecution): ClientHttpResponse {
        if (req.uri.path.contains("monitoring")) {
            return execution.execute(req, body)
        }
        if (!body.isEmpty()) {
            log.trace(CONFIDENTIAL, "Body for {} {} : {} ", req.method, req.uri, String(body))
        }
        val res = execution.execute(req, body)
        log.trace(CONFIDENTIAL, "Response status for {} {}: {}", req.method, req.uri, res.statusCode)
        return res
    }
}