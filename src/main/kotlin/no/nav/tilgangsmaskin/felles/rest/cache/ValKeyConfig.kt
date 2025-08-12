package no.nav.tilgangsmaskin.felles.rest.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
data class ValKeyConfig(private val env: Environment, @Value("\${valkey.suffix}") val suffix: String) {


    val port: String  get() = env.getRequiredProperty("valkey.port.$suffix")
    val host: String  get() = env.getRequiredProperty("valkey.host.$suffix")

}