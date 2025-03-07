package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AktørId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson
import java.time.LocalDate

data class PdlPipRespons(val aktoerId: AktørId, val person: PdlPipPerson, val identer: Identer, val geografiskTilknytning: PdlGeoTilknytning)  {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class PdlPipPerson(
        val adressebeskyttelse: List<Gradering> = emptyList(),
        val foedsel: List<Fødsel> = emptyList(),
        val doedsfall: List<Dødsfall> = emptyList(),
        val familierelasjoner: List<Familierelasjon> = emptyList())  {

        data class Gradering(val gradering: AdressebeskyttelseGradering)  {
            enum class AdressebeskyttelseGradering { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG}
        }

        data class Fødsel(val foedselsdato: LocalDate)
        data class Dødsfall(val doedsdato: LocalDate)
        data class Familierelasjon(val relatertPersonsIdent: BrukerId? = null, val relatertPersonsRolle: FamilieRelasjonRolle? = null, val minRolleForPerson: FamilieRelasjonRolle? = null) {
            enum class FamilieRelasjonRolle  {MOR,FAR,BARN}
        }
    }
    data class Identer(val identer: List<Ident>) {
        data class Ident(val ident: String, val historisk: Boolean, val gruppe: IdentGruppe) {
            enum class IdentGruppe { AKTORID, FOLKEREGISTERIDENT }
        }
    }
}

data class PdlPipBulkRespons(val respons: Map<String, PdlPipPerson>)





