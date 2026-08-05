package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class ForeldreOgBarnRegelTest : RegelMotorTestBase() {
    init {
        Given("ansatt er forelder til bruker") {
            val ansattBrukerId = BrukerId("08526835644")
            val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(brukerId)).build()
            val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
            val barn = BrukerBuilder(brukerId).build()

            When("regler evalueres") {
                Then("avvises av ForeldreOgBarnRegel") {
                    forventAvvistAv<ForeldreOgBarnRegel>(ansatt, barn)
                }
            }
        }

        Given("ansatt er barn av bruker") {
            val ansattBrukerId = BrukerId("08526835644")
            val ansattBruker = BrukerBuilder(ansattBrukerId).far(brukerId).build()
            val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
            val far = BrukerBuilder(brukerId).build()

            When("regler evalueres") {
                Then("avvises av ForeldreOgBarnRegel") {
                    forventAvvistAv<ForeldreOgBarnRegel>(ansatt, far)
                }
            }
        }
    }
}

