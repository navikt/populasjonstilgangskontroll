package no.nav.tilgangsmaskin.regler

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.PartnerRegel

class PartnerRegelTest : BehaviorSpec({
    val regel = PartnerRegel()
    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    Given("ansatt er partner med bruker") {
        val ansattBrukerId = BrukerId("08526835644")
        val ansattBruker = BrukerBuilder(ansattBrukerId).partnere(setOf(brukerId)).build()
        val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
        val partner = BrukerBuilder(brukerId).build()

        When("regelen evalueres") {
            Then("avvises av PartnerRegel") {
                regel.evaluer(ansatt, partner).shouldBeFalse()
            }
        }
    }

    Given("ansatt er ikke partner med bruker") {
        val ansattBrukerId = BrukerId("08526835644")
        val annenPartner = BrukerId("08526835648")
        val ansattBruker = BrukerBuilder(ansattBrukerId).partnere(setOf(annenPartner)).build()
        val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
        val bruker = BrukerBuilder(brukerId).build()

        When("regelen evalueres") {
            Then("tilgang gis") {
                regel.evaluer(ansatt, bruker).shouldBeTrue()
            }
        }
    }
})
