package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder

class EgneDataRegelTest : BehaviorSpec({
    val regel = EgneDataRegel()
    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    Given("ansatt forsøker tilgang til egne skjermede data") {
        val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
        val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).bruker(bruker).build()

        When("regelen evalueres") {
            Then("avvises av EgneDataRegel") {
                regel.evaluer(ansatt, bruker).shouldBeFalse()
            }
        }
    }

    Given("ansatt har skjerming, men behandler ikke seg selv") {
        val ansattBruker = BrukerBuilder(BrukerId("08526835644")).build()
        val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).bruker(ansattBruker).build()
        val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()

        When("regelen evalueres") {
            Then("tilgang gis") {
                regel.evaluer(ansatt, bruker).shouldBeTrue()
            }
        }
    }
})
