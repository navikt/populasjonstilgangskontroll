package no.nav.tilgangsmaskin.ansatt

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.requireDigits

@JvmInline
value class Enhetsnummer(@JsonValue val verdi: String) {
    init {
        requireDigits(verdi, 4)
    }
}