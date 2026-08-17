package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.BRUKER_ID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.pdlRespons
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlDødsfall
import java.time.LocalDate

class PdlPersonMapperDodsdatoTest : BehaviorSpec({
    Given("tilPerson - dødsdato") {
        When("ingen dødsfall") {
            Then("returneres null") { tilPerson(BRUKER_ID, pdlRespons()).dødsdato.shouldBeNull() }
        }
        When("ett dødsfall") {
            Then("returneres dødsdato") {
                val dato = LocalDate.of(2024, 1, 15)
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(doedsfall = listOf(PdlDødsfall(dato))))).dødsdato shouldBe dato
            }
        }
        When("flere dødsfall") {
            Then("returneres seneste dødsdato") {
                val tidlig = LocalDate.of(2023, 1, 1)
                val sen = LocalDate.of(2024, 6, 1)
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(doedsfall = listOf(PdlDødsfall(tidlig), PdlDødsfall(sen))))).dødsdato shouldBe sen
            }
        }
        When("dødsfall uten dato") {
            Then("returneres null") {
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(doedsfall = listOf(PdlDødsfall())))).dødsdato.shouldBeNull()
            }
        }
        When("flere dødsfall der noen mangler dato") {
            Then("returneres seneste kjente dødsdato") {
                val dato = LocalDate.of(2024, 3, 1)
                tilPerson(BRUKER_ID, pdlRespons(PdlPerson(doedsfall = listOf(PdlDødsfall(), PdlDødsfall(dato))))).dødsdato shouldBe dato
            }
        }
    }
})
