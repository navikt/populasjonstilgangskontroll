package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class StrengtFortroligUtlandRegelTest : RegelMotorTestBase() {
    init {
        Given("bruker har strengt fortrolig utland-beskyttelse") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()

            When("ansatt mangler nødvendig gruppe") {
                Then("avvises av StrengtFortroligUtlandRegel") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
                }
            }

            When("ansatt er fortrolig") {
                Then("avvises av StrengtFortroligUtlandRegel") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
                }
            }

            When("ansatt er strengt fortrolig") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                    ansatt kanBehandle bruker
                }
            }
        }
    }
}

