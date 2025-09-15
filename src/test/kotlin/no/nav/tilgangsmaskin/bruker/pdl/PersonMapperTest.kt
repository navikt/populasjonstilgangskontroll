package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTBydel
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTKommune
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTLand
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.BYDEL
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UDEFINERT
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.AKTORID
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class PersonMapperTest {

    private val vanligBrukerId = BrukerId("08526835670")
    private val brukerId = BrukerBuilder(vanligBrukerId).build().brukerId.verdi
    private val aktørId = AktørId("1234567890123").verdi

    @Test
    @DisplayName("Test at behandling av brukere med uten  geotilknytning får UdefinertTilknytning")
    fun udefinert() {
        assertThat(tilPerson(brukerId,pdlRespons(geoUdefinert())).geoTilknytning).isInstanceOf(UdefinertTilknytning::class.java)
    }

    @Test
    @DisplayName("Test at behandling av brukere uten geotilknytning får UtenlandskTilknytning")
    fun utland() {
        assertThat(tilPerson(brukerId,pdlRespons(geoUtland())).geoTilknytning).isInstanceOf(UtenlandskTilknytning::class.java)
    }

    @Test
    @DisplayName("Test at behandling av brukere med kommunal geotilknytning får KommuneTilknytning")
    fun kommune() {
        assertThat(tilPerson(brukerId,pdlRespons(geoKommune())).geoTilknytning).isInstanceOf(KommuneTilknytning::class.java)
    }

    @Test
    @DisplayName("Test at behandling av brukere med bydels geotilknytning får BydelTilknytning")
    fun bydel() {
        assertThat(tilPerson(brukerId,pdlRespons(geoBydel())).geoTilknytning).isInstanceOf(BydelTilknytning::class.java)
    }

    private fun geoUtland() = PdlGeografiskTilknytning(UTLAND, gtLand = GTLand("SWE"))
    private fun geoKommune() = PdlGeografiskTilknytning(KOMMUNE, gtKommune = GTKommune("1234"))
    private fun geoBydel() = PdlGeografiskTilknytning(BYDEL, gtBydel = GTBydel("123456"))
    private fun geoUdefinert() = PdlGeografiskTilknytning(UDEFINERT)


    private fun pdlRespons(
            geo: PdlGeografiskTilknytning,
            gradering: PdlAdressebeskyttelseGradering? = null
                          ): PdlRespons {
        val adressebeskyttelse = gradering?.let {
            listOf(PdlAdressebeskyttelse(it))
        } ?: emptyList()
        return PdlRespons(
                PdlPerson(adressebeskyttelse),
                PdlIdenter(listOf(PdlIdent(brukerId, false, FOLKEREGISTERIDENT), PdlIdent(aktørId, false, AKTORID))),
                geo)
    }
}
