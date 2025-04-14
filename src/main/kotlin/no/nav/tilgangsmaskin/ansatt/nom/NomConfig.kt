package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(NOM)
class NomConfig(val topic: String) {

    companion object {
        const val NOM = "nom"
    }
}
