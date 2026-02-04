package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(ENTRAPROXY)
class EntraProxyConfig(
    baseUri: URI = DEFAULT_URI,
    pingPath: String = DEFAULT_PING_PATH,
    enabled: Boolean = true) :  AbstractRestConfig(baseUri, pingPath, GRAPH, enabled) {


    fun brukerURI(navIdent: String) = builder().apply {
        path(ANSATT_PATH)
    }.build(navIdent)

    fun enheterURI(navIdent: String) = builder().apply {
        path(ENHETER_PATH)
    }.build(navIdent)


    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        private val DEFAULT_URI = URI.create("http://entra-proxy.sikkerhetstjenesten")
        const val ENTRAPROXY = "entra-proxy"
        private const val ANSATT_PATH = "/api/v1/ansatt/{navIdent}"
        private const val ENHETER_PATH = "/api/v1/enhet/ansatt/{navIdent}"
        private const val DEFAULT_PING_PATH = "/monitoring/health/liveness"
    }
}