
package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.MockKAnnotations.init
import io.mockk.clearAllMocks
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyEnhet.Enhet
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import java.time.LocalDate.now

class AvdødBrukerRegelTest : BehaviorSpec() {


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
            regel = AvdødBrukerRegel()
        }

        Given("Bruker lever") {
            val bruker = BrukerBuilder(brukerId).build()
            When("regel evalueres") {
                Then("tilgang godkjennes") { regel.evaluer(ansatt, bruker).shouldBeTrue() }
            }
        }

        Given("Bruker er død") {
            val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(1)).build()
            When("regel evalueres") {
                Then("tilgang godkjennes") { regel.evaluer(ansatt, bruker).shouldBeTrue() }
            }
        }

        Given("Bruker er død for opptil ett år siden") {
            val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(1)).build()
            When("dødsdato er mindre enn 6 måneder siden") {
                Then("tilgang godkjennes og ingen telling skjer for 0-6 måneder") {
                    regel.evaluer(ansatt, bruker).shouldBeTrue()
                }
            }
            When("dødsdato er mellom 6 og 12 måneder siden") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(9)).build()
                Then("tilgang godkjennes og ingen telling skjer for 7-12 måneder") {
                    regel.evaluer(ansatt, bruker).shouldBeTrue()
                }
            }
        }

        Given("Bruker er død for mer enn ett år siden og ansatt er ikke i AVDØD-gruppen") {
            val enhet = Enhet(Enhetsnummer("1234"), NAV_TESTKONTOR)
            When("dødsdato er mellom ett og to år siden") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(15)).build()
                Then("tilgang blokkeres, men telles med enhetsnavn for 13-24 måneder") {
                    regel.evaluer(ansatt, bruker).shouldBeFalse()
                }
            }
            When("dødsdato er mer enn to år siden") {
                Then("tilgang blokkeres, men telles med enhetsnavn for mer enn 24 måneder") {
                    val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(30)).build()
                    regel.evaluer(ansatt, bruker).shouldBeFalse()
                }
            }
        }
    }
}
