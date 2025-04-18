package no.nav.tilgangsmaskin.ansatt

import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster
import java.util.*


enum class GlobalGruppe(val property: String, val metadata: BeskrivelseTekster) {
    STRENGT_FORTROLIG("gruppe.strengt", BeskrivelseTekster.STRENGT_FORTROLIG_ADRESSE),
    FORTROLIG("gruppe.fortrolig", BeskrivelseTekster.FORTROLIG_ADRESSE),
    EGEN_ANSATT("gruppe.egenansatt", BeskrivelseTekster.EGNEDATA),
    UKJENT_BOSTED("gruppe.udefinert", BeskrivelseTekster.PERSON_UKJENT),
    BOSTED_UTLAND("gruppe.utland", BeskrivelseTekster.PERSON_UTLAND),
    NASJONAL("gruppe.nasjonal", BeskrivelseTekster.GEOGRAFISK);

    lateinit var id: UUID

    companion object {
        fun setIDs(grupper: Map<String, UUID>) {
            entries.forEach { gruppe ->
                gruppe.id = grupper[gruppe.property] ?: error("Mangler id for ${gruppe.property}")
            }
        }
    }
}
