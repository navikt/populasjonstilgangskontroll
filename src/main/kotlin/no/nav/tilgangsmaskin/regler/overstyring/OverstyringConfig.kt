package no.nav.tilgangsmaskin.regler.overstyring

import no.nav.tilgangsmaskin.regler.overstyring.OverstyringConfig.Companion.OVERSTYRING
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(OVERSTYRING)
class OverstyringConfig(val systemer: Set<String> = setOf("histark","gosys")) {

    companion object {
        const val OVERSTYRING = "overstyring"
    }
}