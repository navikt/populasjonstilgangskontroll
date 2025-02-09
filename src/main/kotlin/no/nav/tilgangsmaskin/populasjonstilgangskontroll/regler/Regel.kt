package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import java.util.function.BiPredicate

interface Regel: BiPredicate<Kandidat, Saksbehandler> {
    val forklaring: RegelForklaring
    data class RegelForklaring(val navn: String, val feilmelding: String, val kode: String, val overstyrbar: Boolean = false)
}