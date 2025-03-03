package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AktørId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Adressebeskyttelse
import java.time.LocalDate

data class PdlPipRespons(val aktoerId: AktørId, val person: Person, val identer: Identer, val geografiskTilknytning: PdlGeoTilknytning)  {

    data class Person(
        val adressebeskyttelse: List<Adressebeskyttelse> = emptyList(),
        val foedsel: List<Foedsel> = emptyList(),
        val doedsfall: List<Doedsfall> = emptyList(),
        val familierelasjoner: List<Familierelasjon> = emptyList())  {

        enum class AdressebeskyttelseGradering { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG}

        @JvmInline
        value class Foedsel(val foedselsdato: String)
        @JvmInline
        value class Doedsfall(val doedsdato: String)
        data class Familierelasjon(val relatertPersonsIdent: BrukerId? = null, val relatertPersonsRolle: FamilieRelasjonRolle? = null, val minRolleForPerson: FamilieRelasjonRolle? = null) {
            enum class FamilieRelasjonRolle  {MOR,FAR,BARN}
        }
    }
    data class Identer(val identer: List<Ident>) {
        data class Ident(val ident: String, val historisk: Boolean, val gruppe: IdentGruppe) {
            enum class IdentGruppe { AKTORID, FOLKEREGISTERIDENT }
        }
    }
    data class GeografiskTilknytning(
        val gtType: String,
        val gtBydel: String,
        val regel: String
    )
}





