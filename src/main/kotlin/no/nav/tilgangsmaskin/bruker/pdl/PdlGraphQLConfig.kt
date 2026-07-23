package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI.create

@Component
class PdlGraphQLConfig(
    @Value("\${PDLGRAPH}") hostname: String,
    @Value("\${texas.scope.pdl-graph}") val scope: String,
) : RestConfig(create("https://$hostname$DEFAULT_PING_PATH"), DEFAULT_PING_PATH, PDLGRAPH) {

    @NoCoverageAnalysis
    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val PDLGRAPH = "pdlgraph"
        private const val BID = "B897"
        val BEHANDLINGSNUMMER = "behandlingsnummer" to BID
        private const val DEFAULT_PING_PATH = "/graphql"
    }
}