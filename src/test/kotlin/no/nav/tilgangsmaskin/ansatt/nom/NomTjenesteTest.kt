package no.nav.tilgangsmaskin.ansatt.nom

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId

// ── Unit tests (no Spring context) ──────────────────────────────────────────

class NomTjenesteTest : DescribeSpec({

    val adapter = mockk<NomJPAAdapter>(relaxed = true)
    val tjeneste = NomTjeneste(adapter)

    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    beforeEach { clearMocks(adapter) }

    describe("fnrForAnsatt") {

        it("returnerer fnr fra adapter") {
            every { adapter.fnrForAnsatt(ansattId.verdi) } returns brukerId

            tjeneste.fnrForAnsatt(ansattId) shouldBe brukerId
        }

        it("returnerer null når ansatt ikke finnes") {
            every { adapter.fnrForAnsatt(ansattId.verdi) } returns null

            tjeneste.fnrForAnsatt(ansattId) shouldBe null
        }

        it("delegerer til adapter med riktig verdi") {
            every { adapter.fnrForAnsatt(ansattId.verdi) } returns brukerId

            tjeneste.fnrForAnsatt(ansattId)

            verify(exactly = 1) { adapter.fnrForAnsatt(ansattId.verdi) }
        }
    }

    describe("ryddOpp") {

        it("returnerer antall slettede rader fra adapter") {
            every { adapter.ryddOpp() } returns 3

            tjeneste.ryddOpp() shouldBe 3
        }

        it("returnerer 0 når ingen rader slettes") {
            every { adapter.ryddOpp() } returns 0

            tjeneste.ryddOpp() shouldBe 0
        }

        it("delegerer til adapter") {
            tjeneste.ryddOpp()

            verify(exactly = 1) { adapter.ryddOpp() }
        }
    }

    describe("lagre") {

        it("delegerer til adapter.upsert") {
            val data = NomAnsattData(ansattId, brukerId, NomAnsattData.ALWAYS)

            tjeneste.lagre(data)

            verify(exactly = 1) { adapter.upsert(data) }
        }
    }
})

// ── Cache tests (Spring context with ConcurrentMapCacheManager) ──────────────

