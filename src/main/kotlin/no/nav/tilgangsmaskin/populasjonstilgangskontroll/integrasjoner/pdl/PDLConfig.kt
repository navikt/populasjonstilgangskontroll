package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(PDLConfig.Companion.PDL)
class PDLConfig(baseUri: URI, val behandlingsNummer : String = BID, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, PDL, enabled) {

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val PDL = "pdl"
        val BEHANDLINGSNUMMER = "behandlingsnummer"
        private const val BID = "B897"
        private const val DEFAULT_PING_PATH = ""
    }
}