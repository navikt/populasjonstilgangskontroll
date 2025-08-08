package no.nav.tilgangsmaskin.felles.rest.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class ValKeyConfig(
    @Value("\${VALKEY_HOST_CACHE1}") val host: String,
    @Value("\${VALKEY_PORT_CACHE1}") val port: String
)