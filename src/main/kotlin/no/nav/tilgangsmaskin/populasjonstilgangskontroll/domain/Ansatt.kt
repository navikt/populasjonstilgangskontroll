package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil
import java.util.*

class Ansatt(val fnr: BrukerId?,  private val attributter: AnsattAttributter, vararg val grupper: EntraGruppe) {
    val ansattId = attributter.ansattId
    fun kanBehandle(id: UUID) = grupper.any { it.id == id }
    data class AnsattAttributter(val id: UUID, val ansattId: AnsattId, val navn: Navn, val enhetsNummer: Enhetsnummer)  {
        data class Navn(val fornavn: String, val etternavn: String, val mellomNavn: String? = null)
    }
    override fun toString() = "${javaClass.simpleName} [attributter=$attributter,grupper=${grupper.contentToString()}]"
}

@JvmInline
value class AnsattId(@JsonValue val verdi: String) {
    init {
        with(verdi) {
            require(length == 7) { "Ugyldig lengde $length for $this, forventet 7" }
            require(first().isUpperCase()) { "Ugyldig første tegn ${first()} for $verdi, må være stor bokstav" }
            require(drop(1).all { it.isDigit() }) { "Ugyldig(e) tegn i $this, forventet kun 6 tall etter første bokstav" }
        }
    }
}

@JvmInline
value class Enhetsnummer(@JsonValue val verdi: String) {
    init {
        ObjectUtil.requireDigits(verdi, 4)
    }
}