package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations.init
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyAnsatt.Enhet
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import java.time.LocalDate.now


class AvdødBrukerRegelTest : BehaviorSpec() {

    @MockK(relaxed = true)
    private lateinit var teller: AvdødTeller

    @MockK
    private lateinit var proxy: EntraProxyTjeneste

    @MockK(relaxed = true)
    private lateinit var auditor: Auditor

    private lateinit var regel: AvdødBrukerRegel
    private val NAV_TESTKONTOR = "NAV Testkontor"
    private val ansattId = AnsattId("Z999999")
    private val ansatt = AnsattBuilder(ansattId).build()
    private val brukerId = BrukerId("08526835670")

    init {
        beforeSpec {
            init(this@AvdødBrukerRegelTest)
        }
        beforeEach {
            clearAllMocks()
            regel = AvdødBrukerRegel(teller, proxy, auditor)
        }

        Given("Bruker er død") {

            Then("regel avviser ikke") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(1)).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }

            Then("oppslag skal telles") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(1)).build()
                regel.skalTelle(ansatt, bruker) shouldBe true
            }
        }

        Given("Bruker lever") {

            val bruker = BrukerBuilder(brukerId).build()

            Then("regel avviser ikke") {
                regel.evaluer(ansatt, bruker) shouldBe true
            }

            Then("oppslag skal ikke telles") {
                regel.skalTelle(ansatt, bruker) shouldBe false
            }
        }

        Given("Bruker er død for opptil ett år siden") {

            Then("bruker UTILGJENGELIG som enhet (slår ikke opp) for 0-6 måneder") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(1)).build()
                regel.tell(ansatt, bruker)
                verify { teller.tell(any(), UTILGJENGELIG) }
                verify(exactly = 0) { proxy.enhet(any()) }
            }

            Then("bruker UTILGJENGELIG som enhet (slår ikke opp) for 7-12 måneder") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(9)).build()
                regel.tell(ansatt, bruker)
                verify { teller.tell(any(), UTILGJENGELIG) }
                verify(exactly = 0) { proxy.enhet(any()) }
            }
        }
        Given("Bruker er død for mer enn 1-2 år siden") {

            Then("henter enhet fra proxy") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(15)).build()
                every { proxy.enhet(ansattId) } returns Enhet(Enhetsnummer("1234"), NAV_TESTKONTOR)
                regel.tell(ansatt, bruker)
                verify { teller.tell(any(), NAV_TESTKONTOR) }
                verify { proxy.enhet(ansattId) }
                verify { auditor.info(any(), null) }
            }

            Then("henter enhet fra proxy for død mer enn 2 år siden") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(30)).build()
                every { proxy.enhet(ansattId) } returns Enhet(Enhetsnummer("1234"), NAV_TESTKONTOR)

                regel.tell(ansatt, bruker)
                verify { teller.tell(any(), NAV_TESTKONTOR) }
                verify { proxy.enhet(ansattId) }
                verify { auditor.info(any(), null) }
            }
        }
        Given("Oppslaget mot proxy feiler") {

            Then("bruker enhetsnavn UTILGJENGELIG") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(15)).build()
                every { proxy.enhet(ansattId) } throws RuntimeException("Shit happens")
                regel.tell(ansatt, bruker)
                verify { teller.tell(any(), UTILGJENGELIG) }
            }
        }
    }
}
