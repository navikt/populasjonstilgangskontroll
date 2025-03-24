package no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.DomainExtensions

@JvmInline
value class AnsattId(@JsonValue val verdi: String) {
    init {
        with(verdi) {
            require(length == 7) { "Ugyldig lengde $length for $this, forventet 7" }
            require(first().isLetter()) { "Ugyldig første tegn ${first()} i $this, må være stor bokstav" }
            DomainExtensions.requireDigits(substring(1), 6)
        }
    }
}