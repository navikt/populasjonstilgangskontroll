package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestConfig
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGraphQLConfig.Companion.PDLGRAPH

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(PDL)
class PdlConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, val personPath: String = DEFAULT_PERSON_PATH, val personBolkPath: String = DEFAULT_PERSON__BOLK_PATH, enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, PDL, enabled) {

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    fun personURI() = uri(personPath)
    fun personerURI() = uri(personBolkPath)

    private fun uri(path: String) = builder().path(path).build()

    companion object {
        const val PDL = "pdl"
        private const val DEFAULT_PING_PATH = "/internal/health/liveness"
        private val DEFAULT_PERSON_PATH = "/api/v1/person"
        private val DEFAULT_PERSON__BOLK_PATH = "/api/v1/personBolk"

    }
}

@ConfigurationProperties(PDLGRAPH)
class PdlGraphQLConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, PDL, enabled) {

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val PDLGRAPH = "pdlgraph"
        private const val BID = "B897"
        val BEHANDLINGSNUMMER = "behandlingsnummer" to BID
        private const val DEFAULT_PING_PATH = ""
    }
}
