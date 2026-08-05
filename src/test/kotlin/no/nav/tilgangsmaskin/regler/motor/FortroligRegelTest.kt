package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class FortroligRegelTest : RegelMotorTestBase() {
    init {
        Given("bruker har fortrolig beskyttelse") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG).build()

            When("ansatt er strengt fortrolig") {
                Then("avvises") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                    forventAvvistAv<FortroligRegel>(ansatt, bruker)
                }
            }

            When("ansatt mangler fortrolig") {
                Then("avvises") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<FortroligRegel>(ansatt, bruker)
                }
            }

            When("ansatt er fortrolig") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    ansatt kanBehandle bruker
                }
            }
        }

        Given("skjermet bruker med fortrolig") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG, SKJERMING).build()
            When("ansatt kun har skjerming") {
                Then("avvises av FortroligRegel") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                    forventAvvistAv<FortroligRegel>(ansatt, bruker)
                }
            }
        }
    }
}
