package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class PdlPipRespons(val person: PdlPipPerson, val identer: PdlPipIdenter = PdlPipIdenter(), val geografiskTilknytning: PdlGeoTilknytning? = null)  {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class PdlPipPerson(
        val adressebeskyttelse: List<PdlPipAdressebeskyttelse> = emptyList(),
        val foedsel: List<PdlPipFødsel> = emptyList(),
        val doedsfall: List<PdlPipDødsfall> = emptyList(),
        val familierelasjoner: List<PdlPipFamilierelasjon> = emptyList())  {

        data class PdlPipAdressebeskyttelse(val gradering: PdlPipAdressebeskyttelseGradering)  {
            enum class PdlPipAdressebeskyttelseGradering { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG,UGRADERT}
        }

        data class PdlPipFødsel(val foedselsdato: LocalDate)
        data class PdlPipDødsfall(val doedsdato: LocalDate)
        data class PdlPipFamilierelasjon(val relatertPersonsIdent: BrukerId? = null, val relatertPersonsRolle: PdlPipFamilieRelasjonRolle? = null, val minRolleForPerson: PdlPipFamilieRelasjonRolle? = null) {
            enum class PdlPipFamilieRelasjonRolle  {MOR,FAR,MEDMOR,MEDFAR,BARN}
        }
    }
    data class PdlPipIdenter(val identer: List<PdlPipIdent> = emptyList()) {
        data class PdlPipIdent(val ident: String, val historisk: Boolean, val gruppe: PdlPipIdentGruppe) {
            enum class PdlPipIdentGruppe { AKTORID, FOLKEREGISTERIDENT }
        }
    }
}



@JsonIgnoreProperties(ignoreUnknown = true)
data class PdlPipRespons1(val person: PdlPipPerson, val identer: PdlPipIdenter = PdlPipIdenter(), val geografiskTilknytning: PdlGeoTilknytning? = null)  {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class PdlPipPerson(
        val adressebeskyttelse: Any, //List<PdlPipAdressebeskyttelse> = emptyList(),
        val foedsel: Any, //List<PdlPipFødsel> = emptyList(),
        val doedsfall: Any, // List<PdlPipDødsfall> = emptyList(),
        val familierelasjoner: Any) /*List<PdlPipFamilierelasjon> = emptyList()) */ {

        data class PdlPipAdressebeskyttelse(val gradering: PdlPipAdressebeskyttelseGradering)  {
            enum class PdlPipAdressebeskyttelseGradering { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG,UGRADERT}
        }

        data class PdlPipFødsel(val foedselsdato: LocalDate)
        data class PdlPipDødsfall(val doedsdato: LocalDate)
        data class PdlPipFamilierelasjon(val relatertPersonsIdent: BrukerId? = null, val relatertPersonsRolle: PdlPipFamilieRelasjonRolle? = null, val minRolleForPerson: PdlPipFamilieRelasjonRolle? = null) {
            enum class PdlPipFamilieRelasjonRolle  {MOR,FAR,MEDMOR,MEDFAR,BARN}
        }
    }
    data class PdlPipIdenter(val identer: List<PdlPipIdent> = emptyList()) {
        data class PdlPipIdent(val ident: String, val historisk: Boolean, val gruppe: PdlPipIdentGruppe) {
            enum class PdlPipIdentGruppe { AKTORID, FOLKEREGISTERIDENT }
        }
    }
}


