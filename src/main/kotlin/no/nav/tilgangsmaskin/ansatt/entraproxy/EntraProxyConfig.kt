package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyClient.Companion.ENTRA_PROXY_PING_PATH
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI

@Component
class EntraProxyConfig(
    @Value("\${spring.http.serviceclient.entraproxy.base-url}")
    baseUrl: URI
) : RestConfig(baseUrl, ENTRA_PROXY_PING_PATH, ENTRAPROXY) {

    companion object {
        const val ENTRAPROXY  = "entraproxy"
    }
}