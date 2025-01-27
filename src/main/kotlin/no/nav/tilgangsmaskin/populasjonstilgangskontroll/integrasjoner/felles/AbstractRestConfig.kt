package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter.Companion.uri
import org.slf4j.LoggerFactory
import java.net.URI

abstract class AbstractRestConfig(val baseUri: URI, val pingPath: String, val name: String = baseUri.host, val isEnabled: Boolean)  {

    protected val log = LoggerFactory.getLogger(javaClass)

    val pingEndpoint = uri(baseUri, pingPath).also {
        log.trace("Ping endpoint: {}", it)
    }
    override fun toString() = "name=$name, pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri"
}