package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.*
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class PdlRespons(
        val person: PdlPerson,
        val identer: PdlIdenter = PdlIdenter(),
        val geografiskTilknytning: PdlGeografiskTilknytning? = null) {

    val aktørId = identer.identer.firstOrNull { it.gruppe == AKTORID }?.ident
        ?: error("Ingen gyldig aktørid funnet")
    val brukerId = identer.identer.firstOrNull { it.gruppe in listOf(FOLKEREGISTERIDENT, NPID) }?.ident
        ?: error("Ingen gyldig ident funnet")

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class PdlPerson(
        val adressebeskyttelse: List<PdlAdressebeskyttelse> = emptyList(),
        val doedsfall: List<PdlDoedsfall> = emptyList(),
        val familierelasjoner: List<PdlFamilierelasjon> = emptyList()) {

        data class PdlAdressebeskyttelse(val gradering: PdlAdressebeskyttelseGradering) {
            enum class PdlAdressebeskyttelseGradering { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG, UGRADERT }
        }

        data class PdlDoedsfall(val doedsdato: LocalDate)
        data class PdlFamilierelasjon(
                val relatertPersonsIdent: BrukerId? = null,
                val relatertPersonsRolle: PdlFamilieRelasjonRolle? = null,
                val minRolleForPerson: PdlFamilieRelasjonRolle? = null) {
            enum class PdlFamilieRelasjonRolle { MOR, FAR, MEDMOR, MEDFAR, BARN }
        }
    }

    data class PdlIdenter(val identer: List<PdlIdent> = emptyList()) {
        data class PdlIdent(val ident: String, val historisk: Boolean, val gruppe: PdlIdentGruppe) {
            enum class PdlIdentGruppe { AKTORID, FOLKEREGISTERIDENT, NPID }
        }
    }
}


data class Partnere(val sivilstand: Set<Sivilstand> = emptySet()) {
    data class Sivilstand(
            val type: Sivilstandstype,
            val relatertVedSivilstand: String?,
            val bekreftelsesdato: String?,
            val gyldigFraOgMed: String?) {
        enum class Sivilstandstype {
            UOPPGITT,
            UGIFT,
            GIFT,
            ENKE_ELLER_ENKEMANN,
            SKILT,
            SEPARERT,
            REGISTRERT_PARTNER,
            SEPARERT_PARTNER,
            SKILT_PARTNER,
            GJENLEVENDE_PARTNER
        }
    }
}



