package no.nav.tilgangsmaskin.ansatt

import java.util.*
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.net.URI

class EntraUrl {

    val gruppeIds = mapOf(
        "gruppe.strengt" to UUID.fromString("11111111-1111-1111-1111-111111111111"),
        "gruppe.egenansatt" to UUID.fromString("22222222-2222-2222-2222-222222222222"),
        "gruppe.utland" to UUID.fromString("33333333-3333-3333-3333-333333333333"),
        "gruppe.nasjonal" to UUID.fromString("44444444-4444-4444-4444-444444444444"),
        "gruppe.fortrolig" to UUID.fromString("55555555-5555-5555-5555-555555555555"),
        "gruppe.udefinert" to UUID.fromString("66666666-6666-6666-6666-666666666666")
    )
    init {
        GlobalGruppe.setIDs(gruppeIds)
    }

    @Test
    @DisplayName("UUIDene til gruppene kommer i rett formatering")
    fun `GrupperCcfURI should correctly format PARAM_NAME_FILTER`() {
        val ansattId = "Z999999"
        val config = EntraConfig(baseUri = URI("https://example.com"))
        val expectedFilter = """id in('11111111-1111-1111-1111-111111111111',
'11111111-1111-1111-1111-111111111111',
'55555555-5555-5555-5555-555555555555',
'22222222-2222-2222-2222-222222222222',
'66666666-6666-6666-6666-666666666666',
'33333333-3333-3333-3333-333333333333',
'44444444-4444-4444-4444-444444444444') or startswith(displayName, '0000-GA-GEO')
""".trimIndent().
        replace(Regex("\\r?\\n"), "")
        val actualFilter = config.grupperURI(ansattId, true).query.substringAfter("\$filter=").substringBefore("&")
        assertEquals(expectedFilter, actualFilter, "PARAM_NAME_FILTER is not formatted correctly")
    }
}