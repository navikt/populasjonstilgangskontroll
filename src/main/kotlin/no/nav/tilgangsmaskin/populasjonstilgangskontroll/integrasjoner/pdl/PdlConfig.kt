package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestConfig
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlConfig.Companion.PDL
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(PDL)
class PdlConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, PDL, enabled) {

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val PDL = "pdl"
        private const val BID = "B897"
        val BEHANDLINGSNUMMER = "behandlingsnummer" to BID
        private const val DEFAULT_PING_PATH = ""
    }
}