package no.nav.tilgangsmaskin.ansatt

import java.util.*
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata


enum class GlobalGruppe(val property: String, val metadata: GruppeMetadata) {
    STRENGT_FORTROLIG("gruppe.strengt", GruppeMetadata.STRENGT_FORTROLIG),
    STRENGT_FORTROLIG_UTLAND("gruppe.strengt", GruppeMetadata.STRENGT_FORTROLIG_UTLAND),
    FORTROLIG("gruppe.fortrolig", GruppeMetadata.FORTROLIG),
    SKJERMING("gruppe.egenansatt", GruppeMetadata.SKJERMING),
    UKJENT_BOSTED("gruppe.udefinert", GruppeMetadata.UKJENT_BOSTED),
    UTENLANDSK("gruppe.utland", GruppeMetadata.UTENLANDSK),
    NASJONAL("gruppe.nasjonal", GruppeMetadata.NASJONAL);

    lateinit var id: UUID

    companion object {
        fun navnFor(id: UUID) = entries.firstOrNull { it.id == id }?.metadata?.name
            ?: error("Fant ikke gruppe med id $id")

        fun getIds() = entries.map { it.id }
        fun setIDs(grupper: Map<String, UUID>) =
            entries.forEach { gruppe ->
                gruppe.id = grupper[gruppe.property] ?: error("Mangler id for ${gruppe.property}")
            }
    }
}
