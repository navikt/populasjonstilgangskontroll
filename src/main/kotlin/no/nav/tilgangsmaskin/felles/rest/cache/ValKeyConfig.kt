package no.nav.tilgangsmaskin.felles.rest.cache

import no.nav.tilgangsmaskin.felles.rest.cache.ValKeyAdapter.Companion.VALKEY
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(VALKEY)
data class ValKeyConfig(val host: String, val port: String, val uri: URI)