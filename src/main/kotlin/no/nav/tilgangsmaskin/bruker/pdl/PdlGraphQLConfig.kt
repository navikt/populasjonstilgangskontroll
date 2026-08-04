package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI.create

@Component
class PdlGraphQLConfig(@Value("\${pdlgraph}") pdlHost: String) : RestConfig(create("https://$pdlHost$DEFAULT_GRAPHQL_PATH"), "", PDLGRAPH) {

    companion object {
        const val PDLGRAPH = "pdlgraph"
        private const val BID = "B897"
        val BEHANDLINGSNUMMER = "behandlingsnummer" to BID
        private const val DEFAULT_GRAPHQL_PATH = "/graphql"
    }
}