package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

data class PdlPerson(val adressebeskyttelse: List<Adressebeskyttelse>, val folkeregisteridentifikator: List<Folkeregisteridentifikator>)  {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Folkeregisteridentifikator(val identifikasjonsnummer: String, val type: String)
    data class Adressebeskyttelse(val gradering: AdressebeskyttelseGradering)  {
        enum class AdressebeskyttelseGradering { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG, @JsonEnumDefaultValue
        UGRADERT}
    }
}
