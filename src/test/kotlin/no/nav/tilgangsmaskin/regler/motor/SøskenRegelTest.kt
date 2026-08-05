package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class SøskenRegelTest : RegelMotorTestBase() {
    init {
        Given("ansatt er søsken til bruker") {
            val ansattBrukerId = BrukerId("08526835644")
            val ansattBruker = BrukerBuilder(ansattBrukerId).søsken(setOf(brukerId)).build()
            val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
            val søsken = BrukerBuilder(brukerId).build()

            When("regler evalueres") {
                Then("avvises av SøskenRegel") {
                    forventAvvistAv<SøskenRegel>(ansatt, søsken)
                }
            }
        }
    }
}

