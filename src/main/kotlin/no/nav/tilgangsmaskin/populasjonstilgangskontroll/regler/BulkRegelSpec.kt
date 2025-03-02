package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId


data class RegelSpec(val brukerId: BrukerId, val type: RegelType) {
    enum class RegelType {
        KJERNE,
        ALLE
    }
}

