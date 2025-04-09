package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.DomainExtensions

@JvmInline
value class AktørId(@JsonValue val verdi: String) {
    init {
        DomainExtensions.requireDigits(verdi, 13)
    }
    override fun toString() = verdi
}