package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonFormat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Adressebeskyttelse
import java.time.LocalDate

data class PdlPipRespons(val aktoerId: AktørId, val person: Person, val identer: Identer, val geografiskTilknytning: PdlGeoTilknytning)

data class Person(
    val adressebeskyttelse: List<Adressebeskyttelse>,
    val foedsel: List<Foedsel>,
    val doedsfall: List<Doedsfall>,
    val familierelasjoner: List<Familierelasjon>
)

data class Foedsel(
    val foedselsdato: String
)

data class Doedsfall(val doedsdato: String
)
@JvmInline
value class AktørId(val aktoerId: String)

data class Familierelasjon(
    val relatertPersonsIdent: String,
    val relatertPersonsRolle: String,
    val minRolleForPerson: String
)
data class Identer(
    val identer: List<Ident>
)

data class Ident(
    val ident: String,
    val historisk: Boolean,
    val gruppe: String
)
