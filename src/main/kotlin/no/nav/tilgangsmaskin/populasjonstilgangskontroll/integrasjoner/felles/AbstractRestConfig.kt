package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import org.springframework.web.util.DefaultUriBuilderFactory
import java.net.URI

abstract class AbstractRestConfig(val baseUri: URI, private val pingPath: String, val name: String = baseUri.host, val isEnabled: Boolean)  {

    protected fun builder() = DefaultUriBuilderFactory("$baseUri").builder()

    val pingEndpoint =   builder().path(pingPath).build()
    override fun toString() = "name=$name, pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri"
}