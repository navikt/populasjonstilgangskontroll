package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import java.time.LocalDate

data class PdlGraphResponse(val data: HentPerson?, val  errors: List<PdlError>?)

data class HentPerson(val hentPerson: Person?)
data class Person(
    val adressebeskyttelse: List<Adressebeskyttelse>,
    val navn: List<Navn>,
    val bostedsadresse: List<Bostedsadresse>,
    val folkeregisteridentifikator: List<Folkeregisteridentifikator>
)

data class Adressebeskyttelse(
    val gradering: AdressebeskyttelseGradering
)

enum class AdressebeskyttelseGradering {
    STRENGT_FORTROLIG_UTLAND,
    STRENGT_FORTROLIG,
    FORTROLIG,
    UGRADERT,
}


data class Navn(
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String
)

data class Bostedsadresse(
    val angittFlyttedato: LocalDate?,
    val coAdressenavn: String?,
    val vegadresse: VegAdresse?,
    val adresse: String?,
    val ukjentBosted: UkjentBosted?
)


data class VegAdresse(
    val matrikkelId: Int?,
    val husnummer: String?,
    val husbokstav: String?,
    val bruksenhetsnummer: String?,
    val adressenavn: String?,
    val kommunenummer: String?,
    val tilleggsnavn: String?,
    val postnummer: String?,
    val koordinater: Koordinater?
)

data class UkjentBosted(
    val bostedskommune: String?
)

data class Koordinater(
    val x: Float?,
    val y: Float?,
    val z: Float?,
    val kvalitet: Int?
)

data class Folkeregisteridentifikator(
    val identifikasjonsnummer: String,
    val type: String,
    val status: String
)
