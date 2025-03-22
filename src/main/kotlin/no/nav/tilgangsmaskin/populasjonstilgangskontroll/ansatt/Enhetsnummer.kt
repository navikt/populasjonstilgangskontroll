package no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil

@JvmInline
value class Enhetsnummer(@JsonValue val verdi: String) {
    init {
        ObjectUtil.requireDigits(verdi, 4)
    }
}