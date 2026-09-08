package no.nav.tilgangsmaskin.regler

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.SøskenRegel

class SøskenRegelTest : BehaviorSpec({
    val regel = SøskenRegel()
    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    Given("ansatt er søsken til bruker") {
        val ansattBrukerId = BrukerId("08526835644")
        val ansattBruker = BrukerBuilder(ansattBrukerId).søsken(setOf(brukerId)).build()
        val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
        val søsken = BrukerBuilder(brukerId).build()

        When("regelen evalueres") {
            Then("avvises av SøskenRegel") {
                regel.evaluer(ansatt, søsken) shouldBe false
            }
        }
    }

    Given("ansatt er ikke søsken til bruker") {
        val ansattBrukerId = BrukerId("08526835644")
        val annetSøsken = BrukerId("08526835648")
        val ansattBruker = BrukerBuilder(ansattBrukerId).søsken(setOf(annetSøsken)).build()
        val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
        val bruker = BrukerBuilder(brukerId).build()

        When("regelen evalueres") {
            Then("tilgang gis") {
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }
    }
})
