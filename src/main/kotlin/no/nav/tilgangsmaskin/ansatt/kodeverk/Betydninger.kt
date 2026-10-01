package no.nav.tilgangsmaskin.ansatt.kodeverk

import no.nav.tilgangsmaskin.ansatt.kodeverk.KodeverkClient.Companion.BOKMÅL
import java.time.LocalDate

data class Betydninger(val betydninger: Map<String, List<Betydning>>) {
    data class Betydning(val gyldigFra: LocalDate, val gyldigTil: LocalDate, val beskrivelser: Map<String, Beskrivelse>) {
        data class Beskrivelse(val term: String, val tekst: String)
    }
}

data class KodeOgNavn(val kode: String, val tekst: String)

fun Betydninger.kodeOgNavn() =
    betydninger.mapTo(sortedSetOf(compareBy(KodeOgNavn::kode))) { (kode, betydninger) ->
        KodeOgNavn(kode, betydninger.single().beskrivelser.getValue(BOKMÅL).tekst)
    }
