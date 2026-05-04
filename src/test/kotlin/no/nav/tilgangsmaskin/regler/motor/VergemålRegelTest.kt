package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations.init
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.LocalAuditor
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class VergemålRegelTest : BehaviorSpec() {

    @MockK(relaxed = true)
    private lateinit var teller: VergemålTeller
    @MockK
    private lateinit var vergemål: VergemålTjeneste
    @SpyK
    private var auditor = LocalAuditor()

    private lateinit var regel: VergemålRegel

    init {
        beforeSpec { init(this@VergemålRegelTest) }
        beforeEach {
            clearAllMocks()
            regel = VergemålRegel(vergemål, auditor, teller)
        }

        Given("ansatt har ikke vergemål for bruker") {
            When("regel evalueres") {
                Then("tilgang godkjennes uten telling og uten audit") {
                    every { vergemål.vergemål(ansattId) } returns emptySet()
                    regel.evaluer(ansatt, bruker) shouldBe true
                    regel.skalTelle(ansatt, bruker) shouldBe false
                    verify(exactly = 0) { auditor.info(any()) }
                    verify(exactly = 0) { teller.tell() }
                }
            }
        }

        Given("ansatt har vergemål for bruker") {
            When("regel evalueres") {
                Then("tilgang godkjennes") {
                    every { vergemål.vergemål(ansattId) } returns setOf(brukerId)
                    regel.evaluer(ansatt, bruker) shouldBe true
                }
                Then("oppslag skal telles") {
                    every { vergemål.vergemål(ansattId) } returns setOf(brukerId)
                    regel.skalTelle(ansatt, bruker) shouldBe true
                }
                Then("auditor informeres med ansattId og brukerId") {
                    every { vergemål.vergemål(ansattId) } returns setOf(brukerId)
                    regel.skalTelle(ansatt, bruker)
                    verify { auditor.info(match { it.contains(ansattId.verdi) && it.contains(brukerId.verdi) }) }
                }
                Then("teller inkrementeres") {
                    every { vergemål.vergemål(ansattId) } returns setOf(brukerId)
                    regel.evaluer(ansatt, bruker)
                    verify { teller.tell() }
                }
            }
        }

        Given("ansatt har vergemål for andre brukere") {
            When("regel evalueres for denne brukeren") {
                Then("tilgang godkjennes uten telling") {
                    every { vergemål.vergemål(ansattId) } returns setOf(BrukerId("20478606614"))
                    regel.evaluer(ansatt, bruker) shouldBe true
                    regel.skalTelle(ansatt, bruker) shouldBe false
                }
            }
        }

        Given("oppslag mot vergemålstjenesten feiler") {
            When("regel evalueres") {
                Then("tilgang godkjennes (skalTelle svelger feilen og returnerer false)") {
                    every { vergemål.vergemål(ansattId) } throws RuntimeException("tjenesten er nede")
                    regel.evaluer(ansatt, bruker) shouldBe true
                    regel.skalTelle(ansatt, bruker) shouldBe false
                    verify(exactly = 0) { teller.tell() }
                }
            }
        }
    }

    companion object {
        private val ansattId = AnsattId("Z999999")
        private val brukerId = BrukerId("08526835670")
        private val ansatt = AnsattBuilder(ansattId).build()
        private val bruker = BrukerBuilder(brukerId).build()
    }
}

