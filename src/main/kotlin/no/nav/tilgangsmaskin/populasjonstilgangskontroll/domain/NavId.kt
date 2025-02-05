package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonValue

@JvmInline
value class NavId(@JsonValue val verdi: String) {
    init {
        with(verdi) {
            require(length == 7) { "Ugyldig lengde $length for $this, forventet 7" }
            require(drop(1).all { it.isDigit() }) { "Ugyldig(e) tegn i $this, forventet kun tall etter første bokstav" }
            require(first().isUpperCase()) { "Ugyldig første tegn ${first()} for $verdi, må være stor bokstav" }
        }
    }
}