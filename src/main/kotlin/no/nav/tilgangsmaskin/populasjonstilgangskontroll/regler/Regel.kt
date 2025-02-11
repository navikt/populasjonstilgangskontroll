package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import java.util.function.BiPredicate

interface Regel: BiPredicate<Kandidat, Saksbehandler> {
    val beskrivelse: RegelBeskrivelse
    data class RegelBeskrivelse(val navn: String,
                                val kode: String,
                                val feilmelding: String = "Saksbehandler %s kan ikke behandle %s",
                                val overstyrbar: Boolean = false)
}