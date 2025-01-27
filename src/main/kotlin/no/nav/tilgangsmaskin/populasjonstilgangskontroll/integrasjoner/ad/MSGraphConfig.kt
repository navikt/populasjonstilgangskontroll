package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.MSGraphConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(GRAPH)
class MSGraphConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, GRAPH, enabled) {

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val GRAPH = "graph"
        private const val DEFAULT_PING_PATH = ""
    }
}