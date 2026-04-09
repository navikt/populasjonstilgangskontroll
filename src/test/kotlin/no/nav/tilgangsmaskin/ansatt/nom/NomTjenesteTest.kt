package no.nav.tilgangsmaskin.ansatt.nom

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId

class NomTjenesteTest : BehaviorSpec({

    val adapter = mockk<NomJPAAdapter>(relaxed = true)
    val tjeneste = NomTjeneste(adapter)

    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    beforeEach { clearMocks(adapter) }

    Given("fnrForAnsatt kalles") {
        When("ansatt finnes") {
            Then("returnerer fnr fra adapter") {
                every { adapter.fnrForAnsatt(ansattId.verdi) } returns brukerId
                tjeneste.fnrForAnsatt(ansattId) shouldBe brukerId
            }
        }

        When("ansatt ikke finnes") {
            Then("returnerer null") {
                every { adapter.fnrForAnsatt(ansattId.verdi) } returns null
                tjeneste.fnrForAnsatt(ansattId) shouldBe null
            }
        }

        Then("delegerer til adapter med riktig verdi") {
            every { adapter.fnrForAnsatt(ansattId.verdi) } returns brukerId
            tjeneste.fnrForAnsatt(ansattId)
            verify(exactly = 1) { adapter.fnrForAnsatt(ansattId.verdi) }
        }
    }

    Given("ryddOpp kalles") {
        When("adapter sletter rader") {
            Then("returnerer antall slettede rader") {
                every { adapter.ryddOpp() } returns 3
                tjeneste.ryddOpp() shouldBe 3
            }
        }

        When("ingen rader slettes") {
            Then("returnerer 0") {
                every { adapter.ryddOpp() } returns 0
                tjeneste.ryddOpp() shouldBe 0
            }
        }

        Then("delegerer til adapter") {
            tjeneste.ryddOpp()
            verify(exactly = 1) { adapter.ryddOpp() }
        }
    }

    Given("lagre kalles") {
        Then("delegerer til adapter.upsert") {
            val data = NomAnsattData(ansattId, brukerId, NomAnsattData.ALWAYS)
            tjeneste.lagre(data)
            verify(exactly = 1) { adapter.upsert(data) }
        }
    }
})