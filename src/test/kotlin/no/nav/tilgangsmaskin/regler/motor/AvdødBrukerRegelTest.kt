
package no.nav.tilgangsmaskin.regler.motor

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.AVDØD
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import java.time.LocalDate.now

class AvdødBrukerRegelTest : BehaviorSpec() {

    private lateinit var regel: AvdødBrukerRegel
    private val ansattUtenAvdod = AnsattBuilder(AnsattId("Z999999")).build()
    private val brukerId = BrukerId("08526835670")

    init {
        beforeEach {
            regel = AvdødBrukerRegel()
        }

        Given("Bruker lever") {
            val bruker = BrukerBuilder(brukerId).build()
            When("regel evalueres") {
                Then("tilgang godkjennes") { regel.evaluer(ansattUtenAvdod, bruker).shouldBeTrue()
                }
            }
        }

        Given("Bruker er død") {
            val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(1)).build()
            When("regel evalueres") {
                Then("tilgang godkjennes") { regel.evaluer(ansattUtenAvdod, bruker).shouldBeTrue()
                }
            }
        }

        Given("Bruker er død for opptil ett år siden") {
            val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(1)).build()
            When("dødsdato er mindre enn 6 måneder siden") {
                Then("tilgang godkjennes og ingen telling skjer for 0-6 måneder") {
                    regel.evaluer(ansattUtenAvdod, bruker).shouldBeTrue()
                }
            }
            When("dødsdato er mellom 6 og 12 måneder siden") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(9)).build()
                Then("tilgang godkjennes og ingen telling skjer for 7-12 måneder") {
                    regel.evaluer(ansattUtenAvdod, bruker).shouldBeTrue()
                }
            }
        }

        Given("Bruker er død for mer enn ett år siden og ansatt er ikke i AVDØD-gruppen") {
            When("dødsdato er mellom ett og to år siden") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(15)).build()
                Then("tilgang blokkeres, men telles med enhetsnavn for 13-24 måneder") {
                    regel.evaluer(ansattUtenAvdod, bruker).shouldBeFalse()
                }
            }
            When("dødsdato er mer enn to år siden") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(30)).build()
                Then("tilgang blokkeres, men telles med enhetsnavn for mer enn 24 måneder") {
                    regel.evaluer(ansattUtenAvdod, bruker).shouldBeFalse()
                }
            }
        }

        Given("Bruker er død for mer enn ett år siden og ansatt er i AVDØD-gruppen") {
            When("dødsdato er mellom ett og to år siden") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(15)).build()
                Then("tilgang godkjennes") {
                    val ansattMedAvdod = AnsattBuilder(AnsattId("Z999998")).medMedlemskapI(AVDØD).build()
                    regel.evaluer(ansattMedAvdod, bruker).shouldBeTrue()
                }
            }

            When("dødsdato er mer enn to år siden") {
                val bruker = BrukerBuilder(brukerId).dødsdato(now().minusMonths(30)).build()
                val ansattMedAvdod = AnsattBuilder(AnsattId("Z999998")).medMedlemskapI(AVDØD).build()
                Then("tilgang godkjennes") {
                    shouldNotThrowAny {
                        regel.evaluer(ansattMedAvdod, bruker).shouldBeTrue()
                    }
                }
            }
        }
    }
}
