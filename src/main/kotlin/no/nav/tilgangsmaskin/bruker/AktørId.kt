package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.requireDigits

data class AktørId(@JsonValue val verdi: String) {
    init {
        requireDigits(verdi, 13)
    }

    override fun toString() = verdi
}