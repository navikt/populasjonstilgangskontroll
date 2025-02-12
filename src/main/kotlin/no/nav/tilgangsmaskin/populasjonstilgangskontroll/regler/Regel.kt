package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import java.util.function.BiPredicate

interface Regel: BiPredicate<Bruker, Ansatt> {
    val beskrivelse: RegelBeskrivelse
    data class RegelBeskrivelse(val navn: String,
                                val kode: String,
                                val feilmelding: String = "Ansatt %s kan ikke behandle bruker %s",
                                val overstyrbar: Boolean = false)
}