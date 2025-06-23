package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(NOM)
class NomConfig(val topic: String): CachableRestConfig {
    override val expireHours= 12L
    override val navn = NOM
    override val cacheNulls=true

    companion object {
        const val NOM = "nom"
    }
}
