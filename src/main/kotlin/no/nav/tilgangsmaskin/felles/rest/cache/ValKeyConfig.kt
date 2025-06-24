package no.nav.tilgangsmaskin.felles.rest.cache

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("valkey")

data class ValKeyConfig(val host: Host, val port: Port) {
    val hostValue = host.cache
    val portValue = port.cache
    data class Host(val cache: String)
    data class Port(val cache: String)
}