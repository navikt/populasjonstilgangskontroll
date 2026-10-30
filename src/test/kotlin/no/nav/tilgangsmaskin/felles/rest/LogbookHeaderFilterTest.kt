package no.nav.tilgangsmaskin.felles.rest

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.zalando.logbook.HttpHeaders

class LogbookHeaderFilterTest {
    private val filter = RestBeanConfig(mockk(), mockk(), mockk()).logbookHeaderFilter()

    @ParameterizedTest
    @ValueSource(strings = ["ident", "IDENT", "X-Identifikator", "x-identifikator"])
    fun `only the last four characters of identifiers remain visible`(name: String) {
        val headers = HttpHeaders.of(name, "ABC12345678", "DEF87654321")

        val filtered = filter.filter(headers)

        filtered[name] shouldBe listOf("XXX5678", "XXX4321")
        headers[name] shouldBe listOf("ABC12345678", "DEF87654321")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "A", "AB", "ABC", "ABCD"])
    fun `short identifiers are fully masked`(value: String) {
        val headers = HttpHeaders.empty()
            .update("ident", value)
            .update("X-Identifikator", value)

        val filtered = filter.filter(headers)

        filtered.getFirst("ident") shouldBe "XXX"
        filtered.getFirst("X-Identifikator") shouldBe "XXX"
    }

    @Test
    fun `five character identifiers hide their first character`() {
        filter.filter(HttpHeaders.of("ident", "ABCDE")).getFirst("ident") shouldBe "XXXBCDE"
    }

    @Test
    fun `authorization remains fully masked`() {
        val headers = HttpHeaders.of("Authorization", "Bearer test-token")

        filter.filter(headers).getFirst("Authorization") shouldBe "XXX"
    }

    @Test
    fun `unrelated headers are unchanged and missing identifiers are not added`() {
        val headers = HttpHeaders.of("Content-Type", "application/json")

        val filtered = filter.filter(headers)

        filtered shouldBe headers
        filtered.containsKey("ident") shouldBe false
        filtered.containsKey("X-Identifikator") shouldBe false
    }
}
