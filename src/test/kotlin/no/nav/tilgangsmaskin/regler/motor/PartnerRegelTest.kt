package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class PartnerRegelTest : RegelMotorTestBase() {
    init {
        Given("ansatt er partner med bruker") {
            val ansattBrukerId = BrukerId("08526835644")
            val ansattBruker = BrukerBuilder(ansattBrukerId).partnere(setOf(brukerId)).build()
            val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
            val partner = BrukerBuilder(brukerId).build()

            When("regler evalueres") {
                Then("avvises av PartnerRegel") {
                    forventAvvistAv<PartnerRegel>(ansatt, partner)
                }
            }
        }

        Given("ansatt er ikke partner med bruker") {
            val ansattBrukerId = BrukerId("08526835644")
            val annenPartner = BrukerId("08526835648")
            val ansattBruker = BrukerBuilder(ansattBrukerId).partnere(setOf(annenPartner)).build()
            val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
            val bruker = BrukerBuilder(brukerId).build()

            When("regler evalueres") {
                Then("tilgang gis") {
                    ansatt kanBehandle bruker
                }
            }
        }
    }
}
