package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class EgneDataRegelTest : RegelMotorTestBase() {
    init {
        Given("ansatt forsøker tilgang til egne skjermede data") {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).bruker(bruker).build()

            When("regler evalueres") {
                Then("avvises av EgneDataRegel") {
                    forventAvvistAv<EgneDataRegel>(ansatt, bruker)
                }
            }
        }

        Given("ansatt har skjerming, men behandler ikke seg selv") {
            val ansattBruker = BrukerBuilder(BrukerId("08526835644")).build()
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).bruker(ansattBruker).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()

            When("regler evalueres") {
                Then("tilgang gis") {
                    ansatt kanBehandle bruker
                }
            }
        }
    }
}
