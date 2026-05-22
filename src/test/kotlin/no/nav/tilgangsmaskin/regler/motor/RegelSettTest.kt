package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.OVERSTYRBAR_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.TELLENDE_REGELTYPE

class RegelSettTest : BehaviorSpec({

    Given("RegelSett") {

        When("opprettet med kjerne-regler") {
            Then("har riktig type og beskrivelse") {
                val regler = listOf(StrengtFortroligRegel(), FortroligRegel())
                val regelSett = RegelSett(KJERNE_REGELTYPE to regler)
                regelSett.type shouldBe KJERNE_REGELTYPE
                regelSett.regler shouldBe regler
                regelSett.beskrivelse shouldBe "Kjerneregelsett"
            }
        }

        When("opprettet med komplett-regler") {
            Then("har riktig beskrivelse") {
                val regelSett = RegelSett(KOMPLETT_REGELTYPE to emptyList())
                regelSett.beskrivelse shouldBe "Komplett regelsett"
            }
        }

        When("opprettet med overstyrbar-type") {
            Then("har riktig beskrivelse") {
                val regelSett = RegelSett(OVERSTYRBAR_REGELTYPE to emptyList())
                regelSett.beskrivelse shouldBe "Overstyrbart regelsett"
            }
        }

        When("opprettet med tellende-type") {
            Then("har riktig beskrivelse") {
                val regelSett = RegelSett(TELLENDE_REGELTYPE to emptyList())
                regelSett.beskrivelse shouldBe "Tellende regelsett"
            }
        }
    }

    Given("RegelType") {

        When("KJERNE_REGELTYPE") {
            Then("har riktig beskrivelse-streng") {
                KJERNE_REGELTYPE.beskrivelse shouldBe RegelSett.KJERNE
            }
        }

        When("KOMPLETT_REGELTYPE") {
            Then("har riktig beskrivelse-streng") {
                KOMPLETT_REGELTYPE.beskrivelse shouldBe RegelSett.KOMPLETT
            }
        }

        When("OVERSTYRBAR_REGELTYPE") {
            Then("har riktig beskrivelse-streng") {
                OVERSTYRBAR_REGELTYPE.beskrivelse shouldBe RegelSett.OVERSTYRBAR
            }
        }

        When("TELLENDE_REGELTYPE") {
            Then("har riktig beskrivelse-streng") {
                TELLENDE_REGELTYPE.beskrivelse shouldBe RegelSett.TELLENDE
            }
        }
    }

    Given("EvalueringType") {
        When("verdiene sjekkes") {
            Then("BULK og ENKELT finnes") {
                EvalueringType.BULK.name shouldBe "BULK"
                EvalueringType.ENKELT.name shouldBe "ENKELT"
            }
        }
    }
})
