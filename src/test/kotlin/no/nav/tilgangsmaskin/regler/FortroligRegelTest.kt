package no.nav.tilgangsmaskin.regler

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.FortroligRegel

class FortroligRegelTest : BehaviorSpec({
    val regel = FortroligRegel()
    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    Given("bruker har fortrolig beskyttelse") {
        val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG).build()

        When("ansatt er strengt fortrolig") {
            Then("avvises") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                regel.evaluer(ansatt, bruker).shouldBeFalse()
            }
        }

        When("ansatt mangler fortrolig") {
            Then("avvises") {
                val ansatt = AnsattBuilder(ansattId).build()
                regel.evaluer(ansatt, bruker).shouldBeFalse()
            }
        }

        When("ansatt er fortrolig") {
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                regel.evaluer(ansatt, bruker).shouldBeTrue()
            }
        }
    }

    Given("skjermet bruker med fortrolig") {
        val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG, SKJERMING).build()
        When("ansatt kun har skjerming") {
            Then("avvises av FortroligRegel") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
                regel.evaluer(ansatt, bruker).shouldBeFalse()
            }
        }
    }
})
