package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class UtlandRegelTest : RegelMotorTestBase() {
    init {
        Given("bruker er bosatt i utlandet") {
            val bruker = BrukerBuilder(brukerId).gt(UtenlandskTilknytning()).build()

            When("ansatt mangler utenlandsk gruppe") {
                Then("avvises av UtlandRegel") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<UtlandRegel>(ansatt, bruker)
                }
            }

            When("ansatt er medlem av utenlandsk") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(UTENLANDSK).build()
                    ansatt kanBehandle bruker
                }
            }
        }
    }
}

