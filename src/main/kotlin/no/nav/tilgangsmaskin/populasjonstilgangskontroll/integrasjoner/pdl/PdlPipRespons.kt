package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AktørId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import java.time.LocalDate

data class PdlPipRespons(val aktoerId: AktørId,val person: PdlPipPerson, val identer: PdlPipIdenter, val geografiskTilknytning: PdlGeoTilknytning)  {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class PdlPipPerson(
        val adressebeskyttelse: List<PdlPipAdressebeskyttelse> = emptyList(),
        val foedsel: List<PdlPipFødsel> = emptyList(),
        val doedsfall: List<PdlPipDødsfall> = emptyList(),
        val familierelasjoner: List<PdlPipFamilierelasjon> = emptyList())  {

        data class PdlPipAdressebeskyttelse(val gradering: PdlPipAdressebeskyttelseGradering)  {
            enum class PdlPipAdressebeskyttelseGradering { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG}
        }

        data class PdlPipFødsel(val foedselsdato: LocalDate)
        data class PdlPipDødsfall(val doedsdato: LocalDate)
        data class PdlPipFamilierelasjon(val relatertPersonsIdent: BrukerId? = null, val relatertPersonsRolle: PdlPipFamilieRelasjonRolle? = null, val minRolleForPerson: PdlPipFamilieRelasjonRolle? = null) {
            enum class PdlPipFamilieRelasjonRolle  {MOR,FAR,MEDMOR,MEDFAR,BARN}
        }
    }
    data class PdlPipIdenter(val identer: List<PdlPipIdent>) {
        data class PdlPipIdent(val ident: String, val historisk: Boolean, val gruppe: PdlPipIdentGruppe) {
            enum class PdlPipIdentGruppe { AKTORID, FOLKEREGISTERIDENT }
        }
    }
}





