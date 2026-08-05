package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class StrengtFortroligRegelTest : RegelMotorTestBase() {
    init {
        Given("bruker har strengt fortrolig beskyttelse") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()

            When("ansatt mangler gruppen") {
                Then("tilgang avvises") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
                }
            }

            When("ansatt kun er fortrolig") {
                Then("tilgang avvises") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
                }
            }

            When("ansatt er medlem av strengt fortrolig") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                    ansatt kanBehandle bruker
                }
            }
        }

        Given("skjermet bruker med strengt fortrolig") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()

            When("ansatt mangler strengt fortrolig") {
                Then("avvises av StrengtFortroligRegel") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                    forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
                }
            }
        }
    }
}

