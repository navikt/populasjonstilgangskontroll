package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.graph.EntraProxyConfig.Companion.PROXY
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(PROXY)
class EntraProxyConfig(baseUri: URI = DEFAULT_BASE_URI,
                       pingPath: String = DEFAULT_PING_PATH,
                       enabled: Boolean = true)  : AbstractRestConfig(baseUri, pingPath, PROXY, enabled) {

    companion object {
        private  val DEFAULT_BASE_URI = URI.create("http://entra-proxy.sikkerhetstjenesten")
        private const val PREFIX = "/api/v1/"
        const val DEFAULT_PING_PATH = "/monitoring/health/liveness"
        const val PROXY = "proxy"
    }

    fun temaURI() = builder().apply {
        path("${PREFIX}tema")
    }.build()

    fun enhetURI() = builder().apply {
        path("${PREFIX}enhet")
    }.build()

    fun temaURI(tema: String) = builder().apply {
        path("${PREFIX}tema/$tema")
    }.build()

    fun enhetURI(enhet: String) = builder().apply {
        path("${PREFIX}enhet/$enhet")
    }.build()

    fun ansattURI() = builder().apply {
        path("${PREFIX}ansatt")
    }.build()

    fun ansattURI(ansattId: String) = builder().apply {
        path("${PREFIX}ansatt/$ansattId")
    }.build()
}