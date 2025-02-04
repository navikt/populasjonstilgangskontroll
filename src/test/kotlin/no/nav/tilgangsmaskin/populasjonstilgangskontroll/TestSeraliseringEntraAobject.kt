package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.EntraGrupperBolk

//@JsonTest
class TestSeraliseringEntraAobject {
    val json = """ {
        "@odata.nextLink": "http://www.vg.no",
        "@odata.context": "https://graph.microsoft.com/v1.0/xxxxx#directoryObjects(id,displayName)",
    "value": [
        {
            "@odata.type": "#microsoft.graph.group",
            "id": "5ef775f2-61f8-4283-bf3d-8d03f428aa14",
            "displayName": "0000-GA-Strengt_Fortrolig_Adresse"
        },
        {
            "@odata.type": "#microsoft.graph.group",
            "id": "dab3f549-f5f0-4a9c-9f5b-1f6a15ae8424",
            "displayName": "0000-GA-Tilleggsstonader-Beslutter"
        }

    ]
   }
}
    """.trimIndent()
   //@Autowired
     val objectMapper = jacksonObjectMapper()

    @Test

   fun test() {
        val adGrupper = objectMapper.readValue(json, EntraGrupperBolk::class.java)
        println(adGrupper)
    }
}