package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.NPID
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class PdlRespons(val person: PdlPerson, val identer: PdlIdenter = PdlIdenter(), val geografiskTilknytning: PdlGeografiskTilknytning? = null)  {

    val brukerId = identer.identer.firstOrNull { it.gruppe in listOf(FOLKEREGISTERIDENT, NPID) }?.ident
        ?: throw IllegalStateException("Ingen gyldig identer funnet")

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class PdlPerson(
        val adressebeskyttelse: List<PdlAdressebeskyttelse> = emptyList(),
        val foedsel: List<PdlFødsel> = emptyList(),
        val doedsfall: List<PdlDødsfall> = emptyList(),
        val familierelasjoner: List<PdlFamilierelasjon> = emptyList())  {

        data class PdlAdressebeskyttelse(val gradering: PdlAdressebeskyttelseGradering)  {
            enum class PdlAdressebeskyttelseGradering { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG,UGRADERT}
        }

        data class PdlFødsel(val foedselsdato: LocalDate)
        data class PdlDødsfall(val doedsdato: LocalDate)
        data class PdlFamilierelasjon(val relatertPersonsIdent: BrukerId? = null, val relatertPersonsRolle: PdlFamilieRelasjonRolle? = null, val minRolleForPerson: PdlFamilieRelasjonRolle? = null) {
            enum class PdlFamilieRelasjonRolle  {MOR,FAR,MEDMOR,MEDFAR,BARN}
        }
    }
    data class PdlIdenter(val identer: List<PdlIdent> = emptyList()) {
        data class PdlIdent(val ident: String, val historisk: Boolean, val gruppe: PdlIdentGruppe) {
            enum class PdlIdentGruppe { AKTORID, FOLKEREGISTERIDENT, NPID }
        }
    }
}


