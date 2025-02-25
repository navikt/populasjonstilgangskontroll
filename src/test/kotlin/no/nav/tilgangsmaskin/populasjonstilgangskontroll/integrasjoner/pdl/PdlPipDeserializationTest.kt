package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestApp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.test.context.ContextConfiguration
import kotlin.test.Test
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId

@JsonTest
@ContextConfiguration(classes= [TestApp::class])
class PdlPipDeserializationTest {

    @Autowired
    lateinit var mapper: ObjectMapper

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
          "relatertPersonsIdent": "26014401260"
        },
        {
          "relatertPersonsIdent": "08074401156"
        }
      ]
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
"""

    @Test
    fun deserialization() {
        println(mapper.readValue<Map<BrukerId, PdlPipRespons>>(json))
    }

}