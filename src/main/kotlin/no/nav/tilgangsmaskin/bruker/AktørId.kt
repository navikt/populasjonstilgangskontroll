package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.requireDigits

data class AktørId(@JsonValue val verdi: String) {
    init {
        requireDigits(verdi, AKTØRID_LENGTH)
    }

    override fun toString() = verdi

    companion object {
        const val AKTØRID_LENGTH = 13
    }
}