package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class UkjentBostedRegelTest : RegelMotorTestBase() {
    init {
        Given("bruker har ukjent bosted") {
            val bruker = BrukerBuilder(brukerId, UkjentBosted()).kreverMedlemskapI(UKJENT_BOSTED).build()

            When("ansatt er medlem av ukjent bosted") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(UKJENT_BOSTED).build()
                    ansatt kanBehandle bruker
                }
            }

            When("ansatt mangler gruppen") {
                Then("avvises av UkjentBostedRegel") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<UkjentBostedRegel>(ansatt, bruker)
                }
            }
        }
    }
}

