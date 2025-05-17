package no.nav.tilgangsmaskin.felles.rest

import java.net.URI
import org.springframework.web.util.DefaultUriBuilderFactory

abstract class AbstractRestConfig(
        val baseUri: URI,
        private val pingPath: String = "",
        val name: String,
        val isEnabled: Boolean = true) {

    protected fun builder() = DefaultUriBuilderFactory("$baseUri").builder()

    protected fun uri(path: String) = builder().path(path).build()

    val pingEndpoint = builder().path(pingPath).build()
    override fun toString() = "name=$name, pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri"
}

