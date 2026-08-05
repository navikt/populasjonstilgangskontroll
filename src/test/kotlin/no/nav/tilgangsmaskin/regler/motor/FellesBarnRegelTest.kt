package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class FellesBarnRegelTest : RegelMotorTestBase() {
    init {
        Given("ansatt har felles barn med bruker") {
            val ansattBrukerId = BrukerId("08526835644")
            val barn = BrukerId("08526835649")
            val ansattBruker = BrukerBuilder(ansattBrukerId).barn(setOf(barn)).build()
            val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
            val mor = BrukerBuilder(brukerId).barn(setOf(barn)).build()

            When("regler evalueres") {
                Then("avvises av FellesBarnRegel") {
                    forventAvvistAv<FellesBarnRegel>(ansatt, mor)
                }
            }
        }
    }
}

