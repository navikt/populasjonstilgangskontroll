package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestConfig
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipConfig.Companion.PDLPIP
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(PDLPIP)
class PdlPipConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, val personPath: String = DEFAULT_PERSON_PATH,val personBolkPath: String = DEFAULT_PERSON__BOLK_PATH,enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, PDLPIP, enabled) {

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    fun personURI() = uri(personPath)
    fun personBolkURI() =uri(personBolkPath)

    private fun uri(path: String) = builder().path(path).build()



    companion object {
        const val PDLPIP = "pdlpip"
        private const val DEFAULT_PING_PATH = "/internal/health/liveness"
        private val DEFAULT_PERSON_PATH = "/api/v1/person"
        private val DEFAULT_PERSON__BOLK_PATH = "/api/v1/personBolk"

    }
}