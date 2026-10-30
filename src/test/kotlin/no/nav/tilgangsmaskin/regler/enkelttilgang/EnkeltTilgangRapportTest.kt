package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class EnkeltTilgangRapportTest : BehaviorSpec({
    Given("rapportoppslag ved månedsskiftet i Oslo") {
        Then("brukes starten av inneværende måned og fødselsnummerfilteret beholdes") {
            val adapter = mockk<EnkeltTilgangJPAAdapter>()
            val maanedsstart = Instant.parse("2026-09-30T22:00:00Z")
            val ekskluderteFnr = setOf("10108000398")
            every { adapter.ikkeRapporterte(maanedsstart, ekskluderteFnr) } returns emptySet()
            val tjeneste =
                EnkeltTilgangTjeneste(
                    mockk(),
                    mockk(),
                    mockk(),
                    adapter,
                    mockk(),
                    mockk(),
                    Clock.fixed(Instant.parse("2026-09-30T22:30:00Z"), ZoneOffset.UTC),
                    mockk(),
                    mockk(),
                    mockk(),
                )

            tjeneste.ikkeRapportertePrLeder(ekskluderteFnr).shouldBeEmpty()

            verify(exactly = 1) { adapter.ikkeRapporterte(maanedsstart, ekskluderteFnr) }
        }
    }
})
