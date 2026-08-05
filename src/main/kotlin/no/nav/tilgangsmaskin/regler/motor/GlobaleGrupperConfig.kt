package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe
import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.*

@ConfigurationProperties("gruppe")
data class GlobaleGrupperConfig(val strengt: UUID, val nasjonal: UUID, val utland: UUID,
                                val udefinert: UUID, val fortrolig: UUID, val egenansatt: UUID, val dead: UUID) {
    init {
        EntraGlobalGruppe.setIDs(
            mapOf(
                "gruppe.dead" to dead,
                "gruppe.strengt" to strengt,
                "gruppe.nasjonal" to nasjonal,
                "gruppe.utland" to utland,
                "gruppe.udefinert" to udefinert,
                "gruppe.fortrolig" to fortrolig,
                "gruppe.egenansatt" to egenansatt))
    }
}