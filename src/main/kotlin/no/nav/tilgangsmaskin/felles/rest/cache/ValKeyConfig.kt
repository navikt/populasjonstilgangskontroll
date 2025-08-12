package no.nav.tilgangsmaskin.felles.rest.cache

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "valkey")
data class ValKeyConfig(val host: String, val port: String)