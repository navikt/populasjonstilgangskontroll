package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.databind.ObjectMapper
import com.neovisionaries.i18n.CountryCode.SE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTKommune
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTLand
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson.PdlPipAdressebeskyttelse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson.PdlPipAdressebeskyttelse.PdlPipAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestApp
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.TEST
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration


@ActiveProfiles(TEST)
@JsonTest
@ContextConfiguration(classes = [TestApp::class])

class PdlPipTilBrukerMapperTest {

    @Autowired
    lateinit var mapper: ObjectMapper

    private val brukerId = TestData.vanligBruker.brukerId

    @Test
    @DisplayName("Test at behandling av brukere med STRENGT_FORTROLIG_UTLAND  krever medlemsskap i STRENGT_FORTROLIG_GRUPPE fra ansatt og at geotilknytning er UtenlandskTilknytning")
    fun strengtFortroligUtland()   {
        with(PdlPipTilBrukerMapper.tilBruker(brukerId,pipRespons(PdlPipAdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND), false)) {
            assertThat(gruppeKrav).containsExactly(STRENGT_FORTROLIG_GRUPPE)
            assertThat(geoTilknytning).isInstanceOf(UtenlandskTilknytning::class.java)
        }
    }
    @Test
    @DisplayName("Test at behandling av brukere med STRENGT_FORTROLIG vil kreve medlemsskap i STRENGT_FORTROLIG_GRUPPE for ansatt og at geotilknytning er KommuneTilknytning")
    fun strengtFortroligKommune()   {
        with(PdlPipTilBrukerMapper.tilBruker(brukerId,pipRespons(PdlPipAdressebeskyttelseGradering.STRENGT_FORTROLIG,geoKommune()), false)) {
            assertThat(gruppeKrav).containsExactly(STRENGT_FORTROLIG_GRUPPE)
            assertThat(geoTilknytning).isInstanceOf(KommuneTilknytning::class.java)
        }
    }
    @Test
    @DisplayName("Test at behandling av brukere med EGEN_ANSATT vil kreve medlemsskap i EGEN_ANSATT_GRUPPE for ansatt")
    fun egenAnsatt()   {
        with(PdlPipTilBrukerMapper.tilBruker(brukerId,pipRespons(), true)) {
            assertThat(gruppeKrav).containsExactly(EGEN_ANSATT_GRUPPE)
        }
    }
    @Test
    @DisplayName("Test at behandling av brukere med EGEN_ANSATT og STRENGT_FORTROLIG vil kreve medlemsskap i EGEN_ANSATT_GRUPPE og STRENGT_FORTROLIG_GRUPPE for ansatt")
    fun egenAnsattKode6()   {
        with(PdlPipTilBrukerMapper.tilBruker(brukerId,pipRespons(PdlPipAdressebeskyttelseGradering.STRENGT_FORTROLIG),  true)) {
            assertThat(gruppeKrav).containsExactlyInAnyOrder(EGEN_ANSATT_GRUPPE,STRENGT_FORTROLIG_GRUPPE)
        }
    }
    @Test
    @DisplayName("Test at behandling av brukere med EGEN_ANSATT og FORTROLIG vil kreve medlemsskap i EGEN_ANSATT_GRUPPE og FORTROLIG_GRUPPE for ansatt")
    fun egenAnsattKode7()   {
        with(PdlPipTilBrukerMapper.tilBruker(brukerId,pipRespons(PdlPipAdressebeskyttelseGradering.FORTROLIG), true)) {
            assertThat(gruppeKrav).containsExactlyInAnyOrder(EGEN_ANSATT_GRUPPE,FORTROLIG_GRUPPE)
        }
    }

    private fun geoUtland() = PdlGeoTilknytning(PdlGeoTilknytning.GTType.UTLAND, gtLand = GTLand(SE.alpha3))
    private fun geoKommune() = PdlGeoTilknytning(KOMMUNE, gtKommune = GTKommune("1234"))


    fun pipRespons(gradering: PdlPipAdressebeskyttelseGradering? = null, geo: PdlGeoTilknytning = geoUtland()) : PdlPipRespons {
        val adressebeskyttelse = gradering?.let {
             listOf(PdlPipAdressebeskyttelse(it))
         }?: emptyList()
        return PdlPipRespons(PdlPipPerson(adressebeskyttelse), geografiskTilknytning = geo)
    }

   @Test
    fun jall() {
        val json = """
            {
  "10108000398": {
    "aktoerId": "1000096233942",
    "person": {
      "adressebeskyttelse": [],
      "foedsel": [
        {
          "foedselsdato": "1980-10-10"
        }
      ],
      "doedsfall": [],
      "familierelasjoner": [
        {
          "relatertPersonsIdent": "26014401260",
          "relatertPersonsRolle": "MOR",
          "minRolleForPerson": "BARN"
        },
        {
          "relatertPersonsIdent": "08074401156",
          "relatertPersonsRolle": "FAR",
          "minRolleForPerson": "BARN"
        }
      ],
      "rettsligHandleevne": []
    },
    "identer": {
      "identer": [
        {
          "ident": "1000096233942",
          "historisk": false,
          "gruppe": "AKTORID"
        },
        {
          "ident": "10108000398",
          "historisk": false,
          "gruppe": "FOLKEREGISTERIDENT"
        }
      ]
    },
    "geografiskTilknytning": {
      "gtType": "BYDEL",
      "gtBydel": "460108",
      "regel": "3"
    }
  }
}
""".trimIndent()

        val deser =mapper.readValue<Map<String, PdlPipRespons>>(json)
        deser.entries.forEach {
          println(it.value)
        }
    }
}
