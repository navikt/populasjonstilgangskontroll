package no.nav.tilgangsmaskin.ansatt.kodeverk

import no.nav.tilgangsmaskin.ansatt.kodeverk.KodeverkClient.Companion.BOKMÅL
import java.time.LocalDate

data class KodeverkBetydninger(val betydninger: Map<String, List<Betydning>>) {
    data class Betydning(val gyldigFra: LocalDate, val gyldigTil: LocalDate, val beskrivelser: Map<String, Beskrivelse>) {
        data class Beskrivelse(val term: String, val tekst: String)
    }
}

data class KodeverkKode(val kode: String, val tekst: String)

fun KodeverkBetydninger.tilKoder() =
    betydninger.mapTo(mutableSetOf()) { (kode, betydninger) ->
        KodeverkKode(kode, betydninger.single().beskrivelser.getValue(BOKMÅL).tekst)
    }
