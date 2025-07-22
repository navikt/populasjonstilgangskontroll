package no.nav.tilgangsmaskin.felles.rest.cache

import org.springframework.beans.factory.annotation.Value
data class ValKeyConfig(
    @Value("\${VALKEY_HOST_VALKEY_TILGANGSMASKIN_CACHE}") val host: String,
    @Value("\${VALKEY_PORT_VALKEY_TILGANGSMASKIN_CACHE}") val port: String
)