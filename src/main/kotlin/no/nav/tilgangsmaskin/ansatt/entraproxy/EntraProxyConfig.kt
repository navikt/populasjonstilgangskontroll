package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyClient.Companion.PING_PATH
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(ENTRAPROXY)
class EntraProxyConfig : AbstractRestConfig(PROXY_BASE, PING_PATH, GRAPH)
{

    @Generated
    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        val PROXY_BASE = URI.create("http://entra-proxy.sikkerhetstjenesten")
        const val ENTRAPROXY = "entra-proxy"
    }
}