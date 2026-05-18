package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyClient.Companion.ENTRA_PROXY_PING_PATH
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import org.springframework.stereotype.Component
import java.net.URI

@Component
class EntraProxyConfig : RestConfig(ENTRA_PROXY_BASE_URI, ENTRA_PROXY_PING_PATH, GRAPH)
{

    companion object {
        val ENTRA_PROXY_BASE_URI = URI.create("http://entra-proxy.sikkerhetstjenesten")
    }
}