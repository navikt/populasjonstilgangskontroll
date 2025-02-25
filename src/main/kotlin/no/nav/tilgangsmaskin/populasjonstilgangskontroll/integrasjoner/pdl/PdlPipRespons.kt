package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonFormat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Adressebeskyttelse
import java.time.LocalDate

data class PdlPipRespons(val aktoerId: AktørId, val person: Person, val identer: Identer, val geografiskTilknytning: PdlGeoTilknytning)

data class Person(
    val adressebeskyttelse: List<Adressebeskyttelse>,
    val foedsel: Fødsel,
    val doedsfall: List<Dødsfall>,
    val familierelasjoner: Familierelasjon
)

@JvmInline
value class Fødsel(@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") val foedselsdato: List<LocalDate>)
@JvmInline
value class Dødsfall(@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") val doedsdato: LocalDate)

@JvmInline
value class AktørId(val aktoerId: String)

@JvmInline
value class Familierelasjon(val relatertPersonsIdent: List<BrukerId>)

data class Identer(
    val identer: List<Ident>
)

data class Ident(
    val ident: String,
    val historisk: Boolean,
    val gruppe: String
)
