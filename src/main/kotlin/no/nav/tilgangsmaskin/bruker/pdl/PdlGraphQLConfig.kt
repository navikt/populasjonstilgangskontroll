package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(PDLGRAPH)
class PdlGraphQLConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH) :
    AbstractRestConfig(baseUri, pingPath, PdlConfig.PDL) {

    @Generated
    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val PDLGRAPH = "pdlgraph"
        private const val BID = "B897"
        val BEHANDLINGSNUMMER = "behandlingsnummer" to BID
        private const val DEFAULT_PING_PATH = ""
    }
}