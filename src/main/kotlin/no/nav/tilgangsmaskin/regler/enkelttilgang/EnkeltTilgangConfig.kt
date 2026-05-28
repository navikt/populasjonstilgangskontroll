package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangConfig.Companion.OVERSTYRING
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(OVERSTYRING)
class EnkeltTilgangConfig(val systemer: Set<String> = SYSTEMER) {

    companion object {
        const val OVERSTYRING = "overstyring"
        val SYSTEMER = setOf("histark","gosys")
    }
}