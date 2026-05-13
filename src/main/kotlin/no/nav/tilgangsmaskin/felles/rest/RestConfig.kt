package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.Generated
import org.springframework.web.util.DefaultUriBuilderFactory
import java.net.URI

abstract class RestConfig(val baseUri: URI, pingPath: String, val name: String) {

    protected fun builder() = DefaultUriBuilderFactory("$baseUri").builder()

    protected fun uri(path: String) = builder().path(path).build()

    val pingEndpoint = builder().path(pingPath).build()

    @Generated
    override fun toString()  = "${javaClass.simpleName} [name=$name, pingEndpoint=$pingEndpoint,baseUri=$baseUri]"
}

