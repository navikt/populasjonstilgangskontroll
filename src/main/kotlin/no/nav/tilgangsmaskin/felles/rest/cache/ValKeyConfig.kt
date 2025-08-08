package no.nav.tilgangsmaskin.felles.rest.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.core.env.getRequiredProperty
import org.springframework.stereotype.Component

@Component
class ValKeyConfig(environment: Environment) {
    lateinit var host: String
    lateinit var port: String

    init {
        val cacheName = environment.getProperty("nais.cache.name")
        host = environment.getRequiredProperty("valkey.host.$cacheName")
        port = environment.getRequiredProperty("valkey.port.$cacheName")
    }
}
