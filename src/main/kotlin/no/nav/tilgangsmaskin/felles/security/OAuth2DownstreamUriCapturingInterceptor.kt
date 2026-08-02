package no.nav.tilgangsmaskin.felles.security

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor

class OAuth2DownstreamUriCapturingInterceptor : ClientHttpRequestInterceptor {
    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution) =
        try {
            OAuth2DownstreamUriContext.set(request.uri.toString())
            execution.execute(request, body)
        } finally {
            OAuth2DownstreamUriContext.clear()
        }
}