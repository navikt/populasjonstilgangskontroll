package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import org.slf4j.LoggerFactory
import org.springframework.web.util.DefaultUriBuilderFactory
import java.net.URI

abstract class AbstractRestConfig(val baseUri: URI, val pingPath: String, val name: String = baseUri.host, val isEnabled: Boolean)  {

    protected val builder = DefaultUriBuilderFactory("$baseUri").builder()
    protected val log = LoggerFactory.getLogger(javaClass)

    val pingEndpoint =   builder.path(pingPath).build()
    override fun toString() = "name=$name, pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri"
}