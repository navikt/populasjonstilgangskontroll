package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.Clock
import java.time.Instant

class EnkeltTilgangJPAAdapterTest : BehaviorSpec({
    Given("ikke-rapporterte tilganger for ulike brukere") {
        val ekskludertFnr = "10108000398"
        val annetFnr = "08526835670"
        val repo = mockk<EnkeltTilgangRepository>()
        val adapter = EnkeltTilgangJPAAdapter(repo, Clock.systemUTC())
        val createdEtter = Instant.parse("2026-09-30T22:00:00Z")
        val expires = Instant.parse("2026-10-20T21:59:00Z")
        every { repo.findAllByRapportertIsNullAndCreatedAfter(createdEtter) } returns
            listOf(
                EnkeltTilgangEntity("Z123456", ekskludertFnr, "Første begrunnelse", "0105", expires),
                EnkeltTilgangEntity("Z123456", ekskludertFnr, "Andre begrunnelse", "0105", expires),
                EnkeltTilgangEntity("Z123456", annetFnr, "Tredje begrunnelse", "0105", expires),
            )

        Then("beholdes alle tilganger uten eksplisitt filtrering") {
            adapter.ikkeRapporterte(createdEtter).map { it.begrunnelse }.toSet() shouldBe
                setOf("Første begrunnelse", "Andre begrunnelse", "Tredje begrunnelse")
        }

        Then("fjernes alle tilganger for ekskludert fødselsnummer") {
            adapter.ikkeRapporterte(createdEtter, setOf(ekskludertFnr)).map { it.begrunnelse } shouldBe listOf("Tredje begrunnelse")
        }
    }
})
