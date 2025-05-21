package no.nav.tilgangsmaskin.ansatt

import java.util.*
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata
import no.nav.tilgangsmaskin.tilgang.Token


enum class GlobalGruppe(val property: String, val metadata: GruppeMetadata) {
    STRENGT_FORTROLIG("gruppe.strengt", GruppeMetadata.STRENGT_FORTROLIG),
    STRENGT_FORTROLIG_UTLAND("gruppe.strengt", GruppeMetadata.STRENGT_FORTROLIG_UTLAND),
    FORTROLIG("gruppe.fortrolig", GruppeMetadata.FORTROLIG),
    SKJERMING("gruppe.egenansatt", GruppeMetadata.SKJERMING),
    UKJENT_BOSTED("gruppe.udefinert", GruppeMetadata.UKJENT_BOSTED),
    UTENLANDSK("gruppe.utland", GruppeMetadata.UTENLANDSK),
    NASJONAL("gruppe.nasjonal", GruppeMetadata.NASJONAL);

    lateinit var id: UUID

    val entraGruppe get() = EntraGruppe(id)

    companion object {
        private fun navnFor(id: UUID) = entries.find { it.id == id }?.name ?: "Fant ikke gruppenavn for id $id"
        fun uuids() = entries.map { it.id }
        fun setIDs(grupper: Map<String, UUID>) =
            entries.forEach { it.id = grupper[it.property] ?: error("Mangler id for ${it.property}") }

        fun Token.globaleGrupper() = globaleGruppeIds.intersect(uuids()).map { EntraGruppe(it, navnFor(it)) }.toSet()
    }
}
