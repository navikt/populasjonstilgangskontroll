package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyClient.Companion.ENTRA_PROXY_PING_PATH
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import org.springframework.stereotype.Component
import java.net.URI

@Component
class EntraProxyConfig : AbstractRestConfig(PROXY_BASE, ENTRA_PROXY_PING_PATH, GRAPH)
{

    companion object {
        val PROXY_BASE = URI.create("http://entra-proxy.sikkerhetstjenesten")
    }
}