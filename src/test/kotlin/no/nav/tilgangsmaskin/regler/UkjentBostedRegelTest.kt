package no.nav.tilgangsmaskin.regler

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.regler.motor.UkjentBostedRegel

class UkjentBostedRegelTest : BehaviorSpec({
    val regel = UkjentBostedRegel()
    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    Given("bruker har ukjent bosted") {
        val bruker = BrukerBuilder(brukerId, UkjentBosted()).kreverMedlemskapI(UKJENT_BOSTED).build()

        When("ansatt er medlem av ukjent bosted") {
            Then("tilgang gis") {
                val ansatt = AnsattBuilder(ansattId).medMedlemskapI(UKJENT_BOSTED).build()
                regel.evaluer(ansatt, bruker).shouldBeTrue()
            }
        }

        When("ansatt mangler gruppen") {
            Then("avvises av UkjentBostedRegel") {
                val ansatt = AnsattBuilder(ansattId).build()
                regel.evaluer(ansatt, bruker).shouldBeFalse()
            }
        }
    }
})
