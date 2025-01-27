package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import java.net.URI

abstract class AbstractRestConfig(val baseUri: URI, val pingPath: String, val name: String = baseUri.host, val isEnabled: Boolean)  {

    val pingEndpoint = AbstractRestClientAdapter.Companion.uri(baseUri, pingPath)
    override fun toString() = "name=$name, pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri"
}