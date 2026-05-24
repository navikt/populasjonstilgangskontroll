package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.UGRADERT

class PersonGraderingTest : BehaviorSpec({

    Given("erStrengtFortroligUtland") {
        When("listen inneholder STRENGT_FORTROLIG_UTLAND") { Then("returneres true")  { listOf(STRENGT_FORTROLIG_UTLAND).erStrengtFortroligUtland().shouldBeTrue()  } }
        When("listen mangler STRENGT_FORTROLIG_UTLAND")   { Then("returneres false") { listOf(STRENGT_FORTROLIG, FORTROLIG, UGRADERT).erStrengtFortroligUtland() shouldBe false } }
        When("listen er tom")                              { Then("returneres false") { emptyList<Person.Gradering>().erStrengtFortroligUtland() shouldBe false } }
    }

    Given("erStrengtFortrolig") {
        When("listen inneholder STRENGT_FORTROLIG") { Then("returneres true")  { listOf(STRENGT_FORTROLIG).erStrengtFortrolig().shouldBeTrue()  } }
        When("listen mangler STRENGT_FORTROLIG")    { Then("returneres false") { listOf(STRENGT_FORTROLIG_UTLAND, FORTROLIG, UGRADERT).erStrengtFortrolig() shouldBe false } }
        When("listen er tom")                       { Then("returneres false") { emptyList<Person.Gradering>().erStrengtFortrolig() shouldBe false } }
    }

    Given("erFortrolig") {
        When("listen inneholder FORTROLIG") { Then("returneres true")  { listOf(FORTROLIG).erFortrolig().shouldBeTrue()  } }
        When("listen mangler FORTROLIG")    { Then("returneres false") { listOf(STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, UGRADERT).erFortrolig() shouldBe false } }
        When("listen er tom")               { Then("returneres false") { emptyList<Person.Gradering>().erFortrolig() shouldBe false } }
    }
})
