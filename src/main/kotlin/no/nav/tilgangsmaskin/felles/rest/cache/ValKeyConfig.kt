package no.nav.tilgangsmaskin.felles.rest.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class ValKeyConfig(
    @param:Value("\${VALKEY_HOST_CACHE}") val host: String,
    @param:Value("\${VALKEY_PORT_CACHE}") val port: String
)