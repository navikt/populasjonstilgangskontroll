package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations.init
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.LocalAuditor
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class VergemålTellendeRegelTest : BehaviorSpec() {

    @MockK(relaxed = true)
    private lateinit var teller: VergemålTeller
    @MockK
    private lateinit var vergemål: VergemålTjeneste
    @MockK(relaxed = true)
    private var auditor = LocalAuditor()

    private lateinit var regel: VergemålTellendeRegel

    init {
        beforeSpec { init(this@VergemålTellendeRegelTest) }
        beforeEach {
            clearAllMocks()
            regel = VergemålTellendeRegel(vergemål, auditor, teller)
        }

        Given("ansatt har vergemål for bruker") {
            When("sideeffekter verifiseres") {
                Then("skalTelle returnerer true") {
                    every { vergemål.vergemål(ansattId) } returns setOf(brukerId)
                    regel.skalTelle(ansatt, bruker).shouldBeTrue()
                }
                Then("teller inkrementeres") {
                    every { vergemål.vergemål(ansattId) } returns setOf(brukerId)
                    regel.evaluer(ansatt, bruker)
                    verify { teller.tell() }
                }
            }
        }

        Given("ansatt har ikke vergemål for bruker") {
            When("sideeffekter verifiseres") {
                Then("skalTelle returnerer false og ingen audit eller telling") {
                    every { vergemål.vergemål(ansattId) } returns emptySet()
                    regel.skalTelle(ansatt, bruker) shouldBe false
                    verify(exactly = 0) { auditor.info(any()) }
                    verify(exactly = 0) { teller.tell() }
                }
            }
        }

        Given("ansatt har vergemål for andre brukere") {
            When("regel evalueres for denne brukeren") {
                Then("skalTelle returnerer false") {
                    every { vergemål.vergemål(ansattId) } returns setOf(BrukerId("20478606614"))
                    regel.skalTelle(ansatt, bruker) shouldBe false
                }
            }
        }

        Given("oppslag mot vergemålstjenesten feiler") {
            When("sideeffekter verifiseres") {
                Then("skalTelle returnerer false og teller kalles ikke") {
                    every { vergemål.vergemål(ansattId) } throws RuntimeException("tjenesten er nede")
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
