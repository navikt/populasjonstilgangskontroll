
package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations.init
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyEnhet.Enhet
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.felles.utils.LocalAuditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_0_6
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_13_24
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_7_12
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_OVER_24
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.tilgang.Token
import java.time.LocalDate.now

class AvdødBrukerRegelTest : BehaviorSpec() {

    @MockK(relaxed = true)
    private lateinit var teller: AvdødTeller
    @MockK
    private lateinit var token: Token
    @MockK
    private lateinit var proxy: EntraProxyTjeneste
    @SpyK
    private var auditor = LocalAuditor()

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
            every { token.system } returns "system"
            regel = AvdødBrukerRegel(auditor, teller, proxy, token)
        }

        Given("Bruker lever") {
            val bruker = BrukerBuilder(brukerId).build()
            When("regel evalueres") {
                Then("tilgang godkjennes") { regel.evaluer(ansatt, bruker).shouldBeTrue() }
                And("oppslag skal ikke telles") { regel.skalTelle(ansatt, bruker) shouldBe false }
            }
        }

        Given("Bruker er død") {
            val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(1)).build()
            When("regel evalueres") {
                Then("tilgang godkjennes") { regel.evaluer(ansatt, bruker).shouldBeTrue() }
                And("oppslag skal ikke telles fordi < 1 år") { regel.skalTelle(ansatt, bruker) shouldBe false }
            }
        }

        Given("Bruker er død for opptil ett år siden") {
            val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(1)).build()
            When("dødsdato er mindre enn 6 måneder siden") {
                Then("tilgang godkjennes og ingen telling skjer for 0-6 måneder") {
                    regel.evaluer(ansatt, bruker).shouldBeTrue()
                    verify(exactly = 0) { teller.tell(any<no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode>(), any<String>()) }
                    verify(exactly = 0) { proxy.enhet(ansattId) }
                }
            }
            When("dødsdato er mellom 6 og 12 måneder siden") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(9)).build()
                Then("tilgang godkjennes og ingen telling skjer for 7-12 måneder") {
                    regel.evaluer(ansatt, bruker).shouldBeTrue()
                    verify(exactly = 0) { teller.tell(any<no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode>(), any<String>()) }
                    verify(exactly = 0) { proxy.enhet(ansattId) }
                }
            }
        }

        Given("Bruker er død for mer enn ett år siden og ansatt er ikke i AVDØD-gruppen") {
            val enhet = Enhet(Enhetsnummer("1234"), NAV_TESTKONTOR)
            beforeEach { every { proxy.enhet(ansattId) } returns enhet }
            When("dødsdato er mellom ett og to år siden") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(15)).build()
                Then("tilgang blokkeres, men telles med enhetsnavn for 13-24 måneder") {
                    regel.evaluer(ansatt, bruker).shouldBeFalse()
                    verify { proxy.enhet(ansattId) }
                    verify { teller.tell(MND_13_24, enhet.navn) }
                    verify { auditor.info(any()) }
                }
            }
            When("dødsdato er mer enn to år siden") {
                Then("tilgang blokkeres, men telles med enhetsnavn for mer enn 24 måneder") {
                    val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(30)).build()
                    regel.evaluer(ansatt, bruker).shouldBeFalse()
                    verify { proxy.enhet(ansattId) }
                    verify { teller.tell(MND_OVER_24, enhet.navn) }
                    verify { auditor.info(any()) }
                }
            }
            When("oppslaget mot proxy feiler") {
                Then("tilgang blokkeres og enhetsnavn $UTILGJENGELIG brukes i metrikken") {
                    val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(15)).build()
                    every { proxy.enhet(ansattId) } throws RuntimeException("Shit happens")
                    regel.evaluer(ansatt, bruker).shouldBeFalse()
                    verify { teller.tell(MND_13_24, UTILGJENGELIG) }
                }
            }
        }
    }
}
