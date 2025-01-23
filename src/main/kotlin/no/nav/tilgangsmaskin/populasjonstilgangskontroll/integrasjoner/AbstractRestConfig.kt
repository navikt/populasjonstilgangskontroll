package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.AbstractRestClientAdapter.Companion.uri
import java.net.URI

abstract class AbstractRestConfig(val baseUri: URI, val pingPath: String, val name: String = baseUri.host, val isEnabled: Boolean)  {

    val pingEndpoint = uri(baseUri, pingPath)
    override fun toString() = "name=$name, pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri"
}