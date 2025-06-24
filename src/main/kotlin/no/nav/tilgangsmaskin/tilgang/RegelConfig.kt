package no.nav.tilgangsmaskin.tilgang

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("regler")
data class RegelConfig(val toggles: Map<String,Boolean> = emptyMap()) {
    fun isEnabled(regel: String) = toggles[regel.lowercase() +".enabled"]  ?: true
}