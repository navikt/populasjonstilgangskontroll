package no.nav.tilgangsmaskin.felles.rest.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
data class ValKeyConfig(
    @Value("\${VALKEY_HOST_CACHE1}") val host: String,
    @Value("\${VALKEY_PORT_CACHE1}") val port: String
)

@Component
data class ValKeyConfig1(private val env: Environment, @Value("\${valkey.suffix}") val suffix: String) {

    val port: String  get() = env.getRequiredProperty("valkey.port.$suffix")
    val host: String  get() = env.getRequiredProperty("valkey.host.$suffix")

}