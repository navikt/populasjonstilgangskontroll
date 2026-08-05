package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class SkjermingRegelTest : RegelMotorTestBase() {
    init {
        Given("bruker er skjermet") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()

            When("ansatt er medlem av skjerming") {
                Then("tilgang gis") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                    ansatt kanBehandle bruker
                }
            }

            When("ansatt har ingen spesialtilganger") {
                Then("avvises av SkjermingRegel") {
                    val ansatt = AnsattBuilder(ansattId).build()
                    forventAvvistAv<SkjermingRegel>(ansatt, bruker)
                }
            }

            When("ansatt er fortrolig") {
                Then("avvises av SkjermingRegel") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                    forventAvvistAv<SkjermingRegel>(ansatt, bruker)
                }
            }

            When("ansatt er strengt fortrolig") {
                Then("avvises av SkjermingRegel") {
                    val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                    forventAvvistAv<SkjermingRegel>(ansatt, bruker)
                }
            }
        }
    }
}

