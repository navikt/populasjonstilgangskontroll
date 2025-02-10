package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonValue

@JvmInline
value class Enhetsnummer(@JsonValue val verdi: String) {
    init {
        with(verdi) {
            require(length == 4) { "Ugyldig lengde $length for $this, forventet 4" }
            require(all { it.isDigit()}) { "Ugyldig(e) tegn i $this, forventet kun 4 tall" }
        }
    }

}

