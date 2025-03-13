package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomConfig.Companion.NOM
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(NOM)
class NomConfig(val topic: String) {


    companion object {
        const val NOM = "nom"
    }
}