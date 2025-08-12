package no.nav.tilgangsmaskin.felles.rest.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class ValKeyConfig( @Value("\${valkey.host}") val host: String,
                          @Value("\${valkey.port}") val port: String) {


}