package no.nav.tilgangsmaskin.ansatt.kodeverk

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class KodeverkBetydninger(val betydninger: Map<String, List<Betydning>>)  {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Betydning(val gyldigFra: LocalDate, val gyldigTil: LocalDate, val beskrivelser: Map<String, Beskrivelse>) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Beskrivelse(val term: String, val tekst: String)
    }
}






