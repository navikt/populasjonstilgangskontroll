package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class RegelMotorBasisTilgangTest : RegelMotorTestBase() {
    init {
        Given("bruker krever ingen spesialtilganger") {
            val bruker = BrukerBuilder(brukerId).build()

            When("ansatt er strengt fortrolig") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                    ansatt kanBehandle bruker
                }
            }

            When("ansatt er fortrolig") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    ansatt kanBehandle bruker
                }
            }

            When("ansatt har ingen spesialtilganger") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    ansatt kanBehandle bruker
                }
            }
        }
    }
}

