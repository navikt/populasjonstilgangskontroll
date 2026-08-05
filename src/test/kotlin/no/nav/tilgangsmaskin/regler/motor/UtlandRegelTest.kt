package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class UtlandRegelTest : BehaviorSpec({
    val regel = UtlandRegel()
    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    Given("bruker er bosatt i utlandet") {
        val bruker = BrukerBuilder(brukerId).gt(UtenlandskTilknytning()).build()

        When("ansatt mangler utenlandsk gruppe") {
            Then("avvises av UtlandRegel") {
                val ansatt = AnsattBuilder(ansattId).build()
                regel.evaluer(ansatt, bruker).shouldBeFalse()
            }
        }

        When("ansatt er medlem av utenlandsk") {
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(UTENLANDSK).build()
                regel.evaluer(ansatt, bruker).shouldBeTrue()
            }
        }
    }
})
