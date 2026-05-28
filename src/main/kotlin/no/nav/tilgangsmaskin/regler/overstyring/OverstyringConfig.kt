package no.nav.tilgangsmaskin.regler.overstyring

import no.nav.tilgangsmaskin.regler.overstyring.OverstyringConfig.Companion.OVERSTYRING
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(OVERSTYRING)
class OverstyringConfig(val systemer: Set<String> = SYSTEMER) {

    companion object {
        const val OVERSTYRING = "overstyring"
        val SYSTEMER = setOf("histark","gosys")
    }
}