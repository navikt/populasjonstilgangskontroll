package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class EnkeltTilgangJPAAdapterTest : BehaviorSpec({
    Given("markering av rapporterte tilganger") {
        val repo = mockk<EnkeltTilgangRepository>()
        val now = Instant.parse("2026-10-20T10:00:00Z")
        val adapter = EnkeltTilgangJPAAdapter(repo, Clock.fixed(now, ZoneOffset.UTC), mockk())
        Then("oppdateres alle ID-er samlet uavhengig av NAV-ident med tidspunkt fra klokken") {
            every { repo.setRapportert(setOf(1L, 2L, 3L), now) } returns 3

            adapter.markerRapportert(setOf(1L, 2L, 3L)) shouldBe 3

            verify(exactly = 1) { repo.setRapportert(setOf(1L, 2L, 3L), now) }
        }

        Then("oppdateres ingen rader for en tom samling") {
            adapter.markerRapportert(emptySet()) shouldBe 0
        }
    }

    Given("ikke-rapporterte tilganger for ulike brukere") {
        val ekskludertFnr = "10108000398"
        val annetFnr = "08526835670"
        val repo = mockk<EnkeltTilgangRepository>()
        val adapter = EnkeltTilgangJPAAdapter(repo, Clock.systemUTC(), mockk())
        val createdEtter = Instant.parse("2026-09-30T22:00:00Z")
        val expires = Instant.parse("2026-10-20T21:59:00Z")
        every { repo.findAllByRapportertIsNullAndCreatedAfterAndGtNavnIsNotNull(createdEtter) } returns
            listOf(
                EnkeltTilgangEntity("Z123456", ekskludertFnr, "Første begrunnelse", "0105", expires, gtNavn = "Oslo").apply { id = 1L },
                EnkeltTilgangEntity("Z123456", ekskludertFnr, "Andre begrunnelse", "0105", expires, gtNavn = "Oslo").apply { id = 2L },
                EnkeltTilgangEntity("Z123456", annetFnr, "Tredje begrunnelse", "0105", expires, gtNavn = "Oslo").apply { id = 3L },
            )

        Then("beholdes alle tilganger uten eksplisitt filtrering") {
            adapter.ikkeRapporterte(createdEtter).map { it.databaseId }.toSet() shouldBe setOf(1L, 2L, 3L)
            adapter.ikkeRapporterte(createdEtter).map { it.begrunnelse }.toSet() shouldBe
                setOf("Første begrunnelse", "Andre begrunnelse", "Tredje begrunnelse")
        }

        Then("fjernes alle tilganger for ekskludert fødselsnummer") {
            adapter.ikkeRapporterte(createdEtter, setOf(ekskludertFnr)).map { it.begrunnelse } shouldBe listOf("Tredje begrunnelse")
        }
    }
})
