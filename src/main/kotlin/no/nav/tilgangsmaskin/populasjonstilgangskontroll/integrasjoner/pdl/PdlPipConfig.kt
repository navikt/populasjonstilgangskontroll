package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestConfig
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipConfig.Companion.PDLPIP
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.DEFAULT_SKJERMING_PATH
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(PDLPIP)
class PdlPipConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, val personPath: String = DEFAULT_PERSON_PATH,enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, PDLPIP, enabled) {

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    fun personURI() = builder().path(personPath).build()


    companion object {
        const val PDLPIP = "pdlpip"
        private const val DEFAULT_PING_PATH = ""
        private val DEFAULT_PERSON_PATH = "/api/v1/person"
    }
}