package no.nav.tilgangsmaskin.regler

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.ForeldreOgBarnRegel

class ForeldreOgBarnRegelTest : BehaviorSpec({
    val regel = ForeldreOgBarnRegel()
    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    Given("ansatt er forelder til bruker") {
        val ansattBrukerId = BrukerId("08526835644")
        val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(brukerId)).build()
        val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
        val barn = BrukerBuilder(brukerId).build()

        When("regelen evalueres") {
            Then("avvises av ForeldreOgBarnRegel") {
                regel.evaluer(ansatt, barn).shouldBeFalse()
            }
        }
    }

    Given("ansatt er barn av bruker") {
        val ansattBrukerId = BrukerId("08526835644")
        val ansattBruker = BrukerBuilder(ansattBrukerId).far(brukerId).build()
        val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
        val far = BrukerBuilder(brukerId).build()

        When("regelen evalueres") {
            Then("avvises av ForeldreOgBarnRegel") {
                regel.evaluer(ansatt, far).shouldBeFalse()
            }
        }
    }

    Given("ansatt og bruker er ikke i foreldre/barn-relasjon") {
        val ansattBrukerId = BrukerId("08526835644")
        val ansattBarn = BrukerId("08526835649")
        val bruker = BrukerBuilder(brukerId).build()
        val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(ansattBarn)).build()
        val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()

        When("regelen evalueres") {
            Then("tilgang gis") {
                regel.evaluer(ansatt, bruker).shouldBeTrue()
            }
        }
    }
})
