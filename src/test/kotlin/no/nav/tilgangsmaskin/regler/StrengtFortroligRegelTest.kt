package no.nav.tilgangsmaskin.regler

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.StrengtFortroligRegel

class StrengtFortroligRegelTest : BehaviorSpec({
    val regel = StrengtFortroligRegel()
    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    Given("bruker har strengt fortrolig beskyttelse") {
        val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()

        When("ansatt mangler gruppen") {
            Then("tilgang avvises") {
                val ansatt = AnsattBuilder(ansattId).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }

        When("ansatt kun er fortrolig") {
            Then("tilgang avvises") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }

        When("ansatt er medlem av strengt fortrolig") {
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                regel.evaluer(ansatt, bruker) shouldBe true
            }
        }
    }

    Given("skjermet bruker med strengt fortrolig") {
        val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()

        When("ansatt mangler strengt fortrolig") {
            Then("avvises av StrengtFortroligRegel") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                regel.evaluer(ansatt, bruker) shouldBe false
            }
        }
    }
})
