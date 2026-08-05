package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class StrengtFortroligUtlandRegelTest : BehaviorSpec({
    val regel = StrengtFortroligUtlandRegel()
    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")
    val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()

    Given("bruker har strengt fortrolig utland-beskyttelse") {
        When("ansatt mangler nødvendig gruppe") {
            Then("avvises av StrengtFortroligUtlandRegel") {
                val ansatt = AnsattBuilder(ansattId).build()
                regel.evaluer(ansatt, bruker).shouldBeFalse()
            }
        }

        When("ansatt er fortrolig") {
            Then("avvises av StrengtFortroligUtlandRegel") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
                regel.evaluer(ansatt, bruker).shouldBeFalse()
            }
        }

        When("ansatt er strengt fortrolig") {
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
                regel.evaluer(ansatt, bruker).shouldBeTrue()
            }
        }
    }
})
