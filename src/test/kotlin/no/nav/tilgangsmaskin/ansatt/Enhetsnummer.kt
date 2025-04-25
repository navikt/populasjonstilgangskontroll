package no.nav.tilgangsmaskin.ansatt

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions

@JvmInline
value class Enhetsnummer(@JsonValue val verdi: String) {
    init {
        DomainExtensions.requireDigits(verdi, 4)
    }
}