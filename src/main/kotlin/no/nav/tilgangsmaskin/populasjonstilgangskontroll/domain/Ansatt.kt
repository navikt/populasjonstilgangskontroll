package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.requireDigits
import java.util.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId as AnsattFnr


class Ansatt(val bruker: Bruker? = null,  val identifikatorer: AnsattIdentifikatorer, val grupper: List<EntraGruppe>) {

    @JsonIgnore
    val ansattId = identifikatorer.ansattId
    @JsonIgnore
    val familieMedlemmer = bruker?.familieMedlemmer?.map { it.brukerId } ?: emptyList()

    fun kanBehandle(id: UUID) = grupper.any { it.id == id }
    override fun toString() = "${javaClass.simpleName} [bruker=$bruker,identifikatorer=$identifikatorer,grupper=$grupper]"
}

@JvmInline
value class AnsattId(@JsonValue val verdi: String) {
    init {
        with(verdi) {
            require(length == 7) { "Ugyldig lengde $length for $this, forventet 7" }
            require(first().isLetter()) { "Ugyldig første tegn ${first()} i $this, må være stor bokstav" }
            requireDigits(substring(1), 6)
        }
    }
}

data class AnsattIdentifikatorer(val ansattId: AnsattId, val oid: UUID, val ansattFnr: AnsattFnr? = null)

@JvmInline
value class Enhetsnummer(@JsonValue val verdi: String) {
    init {
        requireDigits(verdi, 4)
    }
}