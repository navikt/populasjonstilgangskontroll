package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import java.time.LocalDate

data class PdlPerson(val adressebeskyttelse: List<Adressebeskyttelse>, val bostedsadresse: List<Bostedsadresse>, val folkeregisteridentifikator: List<Folkeregisteridentifikator>)  {
    data class Folkeregisteridentifikator(val identifikasjonsnummer: String, val type: String)
    data class Adressebeskyttelse(val gradering: AdressebeskyttelseGradering)  {
        enum class AdressebeskyttelseGradering { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG, @JsonEnumDefaultValue
        UGRADERT}
    }
    data class Bostedsadresse(val angittFlyttedato: LocalDate?, val coAdressenavn: String?, val vegadresse: VegAdresse?, val adresse: String?, val ukjentBosted: UkjentBosted?) {
        data class UkjentBosted(val bostedskommune: String?)
        data class VegAdresse(
            val matrikkelId: Int?, val husnummer: String?, val husbokstav: String?, val bruksenhetsnummer: String?,
            val adressenavn: String?, val kommunenummer: String?, val tilleggsnavn: String?, val postnummer: String?, val koordinater: Koordinater?) {
            data class Koordinater(val x: Float?, val y: Float?, val z: Float?, val kvalitet: Int?)
        }
    }
}
