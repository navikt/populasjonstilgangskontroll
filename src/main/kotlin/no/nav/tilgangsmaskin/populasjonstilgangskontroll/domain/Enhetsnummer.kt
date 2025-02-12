package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.requires

@JvmInline
value class Enhetsnummer(@JsonValue val verdi: String) {
    init {
        requires(verdi,4)
    }
}

