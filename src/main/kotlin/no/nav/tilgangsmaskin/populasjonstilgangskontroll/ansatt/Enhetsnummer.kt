package no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.DomainExtensions

@JvmInline
value class Enhetsnummer(@JsonValue val verdi: String) {
    init {
        DomainExtensions.requireDigits(verdi, 4)
    }
}