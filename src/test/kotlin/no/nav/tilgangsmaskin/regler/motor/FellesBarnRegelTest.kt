package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class FellesBarnRegelTest : BehaviorSpec({
    val regel = FellesBarnRegel()
    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    Given("ansatt har felles barn med bruker") {
        val ansattBrukerId = BrukerId("08526835644")
        val barn = BrukerId("08526835649")
        val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(barn)).build()
        val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
        val mor = BrukerBuilder(brukerId).barn(setOf(barn)).build()

        When("regelen evalueres") {
            Then("avvises av FellesBarnRegel") {
                regel.evaluer(ansatt, mor).shouldBeFalse()
            }
        }
    }

    Given("ansatt og bruker har ikke felles barn") {
        val ansattBrukerId = BrukerId("08526835644")
        val ansattBarn = BrukerId("08526835649")
        val brukerBarn = BrukerId("08526835648")
        val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(ansattBarn)).build()
        val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
        val bruker = BrukerBuilder(brukerId).barn(setOf(brukerBarn)).build()

        When("regelen evalueres") {
            Then("tilgang gis") {
                regel.evaluer(ansatt, bruker).shouldBeTrue()
            }
        }
    }
})
