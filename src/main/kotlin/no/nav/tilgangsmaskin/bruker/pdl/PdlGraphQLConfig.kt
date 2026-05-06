package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI.create
import kotlin.jvm.javaClass

@Component
class PdlGraphQLConfig(@Value("\${PDLGRAPH}") hostname: String) :
    AbstractRestConfig(create("https://" + hostname), DEFAULT_PING_PATH, PDLGRAPH) {

    @Generated
    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val PDLGRAPH = "pdlgraph"
        private const val BID = "B897"
        val BEHANDLINGSNUMMER = "behandlingsnummer" to BID
        private const val DEFAULT_PING_PATH = ""
    }
}