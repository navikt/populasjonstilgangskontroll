package no.nav.tilgangsmaskin.ansatt

import java.util.*
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster


enum class GlobalGruppe(val property: String, val metadata: BeskrivelseTekster) {
    STRENGT_FORTROLIG("gruppe.strengt", BeskrivelseTekster.STRENGT_FORTROLIG),
    FORTROLIG("gruppe.fortrolig", BeskrivelseTekster.FORTROLIG),
    SKJERMING("gruppe.egenansatt", BeskrivelseTekster.SKJERMING),
    UKJENT_BOSTED("gruppe.udefinert", BeskrivelseTekster.UKJENT_BOSTED),
    UTENLANDSK("gruppe.utland", BeskrivelseTekster.UTENLANDSK),
    NASJONAL("gruppe.nasjonal", BeskrivelseTekster.NASJONAL);

    lateinit var id: UUID

    companion object {
        fun setIDs(grupper: Map<String, UUID>) =
            entries.forEach { gruppe ->
                gruppe.id = grupper[gruppe.property] ?: error("Mangler id for ${gruppe.property}")
            }
    }
}
