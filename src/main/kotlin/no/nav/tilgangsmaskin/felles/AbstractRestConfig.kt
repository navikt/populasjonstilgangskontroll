package no.nav.tilgangsmaskin.felles

import java.net.URI
import org.springframework.web.util.DefaultUriBuilderFactory

abstract class AbstractRestConfig(
        val baseUri: URI,
        private val pingPath: String = "",
        val name: String = baseUri.host,
        val isEnabled: Boolean = true
) {

    protected fun builder() = DefaultUriBuilderFactory("$baseUri").builder()

    val pingEndpoint = builder().path(pingPath).build()
    override fun toString() = "name=$name, pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri"
}