package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import java.util.function.BiPredicate

interface Regel: BiPredicate<Bruker, Ansatt> {
    val beskrivelse: RegelBeskrivelse
    data class RegelBeskrivelse(val kortNavn: String,
                                val kode: AvvisningBegrunnelse,
                                val overstyrbar: Boolean = false) {
        
    val begrunnelseAnsatt =  "Ansatt %s kan ikke behandle bruker %s grunnet %s"
    }
}