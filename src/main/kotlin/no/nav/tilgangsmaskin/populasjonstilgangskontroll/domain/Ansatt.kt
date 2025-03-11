package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.requireDigits
import java.util.*

class Ansatt(val bruker: Bruker? = null,  val ansattId: AnsattId, val attributter: AnsattAttributter? = null, vararg val grupper: EntraGruppe) {

    @JsonIgnore
    val fnr = bruker?.brukerId
    @JsonIgnore
    val familieMedlemmer = bruker?.familieMedlemmer?.map { it.brukerId } ?: emptyList()

    fun kanBehandle(id: UUID) = grupper.any { it.id == id }
    data class AnsattAttributter(val id: UUID, val ansattId: AnsattId, val enhetsNummer: Enhetsnummer)
    override fun toString() = "${javaClass.simpleName} [attributter=$attributter,grupper=${grupper.contentToString()}]"
}

@JvmInline
value class AnsattId(@JsonValue val verdi: String) {
    init {
        with(verdi) {
            require(length == 7) { "Ugyldig lengde $length for $this, forventet 7" }
            require(first().isUpperCase()) { "Ugyldig første tegn ${first()} i $this, må være stor bokstav" }
            requireDigits(substring(1), 6)
        }
    }
}

@JvmInline
value class Enhetsnummer(@JsonValue val verdi: String) {
    init {
        requireDigits(verdi, 4)
    }
}