package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class VergemålRegelTest : BehaviorSpec({
    val vergemål = mockk<VergemålTjeneste>()
    val regel = VergemålRegel(vergemål)
    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    Given("ansatt har vergemål for bruker") {
        When("vergemål returnerer bruker") {
            Then("avvises av VergemålRegel") {
                every { vergemål.alle(ansattId) } returns setOf(brukerId)
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).build()
                regel.evaluer(ansatt, bruker).shouldBeFalse()
            }
        }
    }

    Given("ansatt ikke har vergemål") {
        When("vergemål returnerer tomt sett") {
            Then("tilgang gis") {
                every { vergemål.alle(ansattId) } returns emptySet()
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).build()
                regel.evaluer(ansatt, bruker).shouldBeTrue()
            }
        }
    }

    Given("vergemålstjenesten feiler") {
        When("oppslag kaster exception") {
            Then("tilgang gis") {
                every { vergemål.alle(ansattId) } throws RuntimeException("tjenesten er nede")
                val ansatt = AnsattBuilder(ansattId).build()
                val bruker = BrukerBuilder(brukerId).build()
                regel.evaluer(ansatt, bruker).shouldBeTrue()
            }
        }
    }
})
