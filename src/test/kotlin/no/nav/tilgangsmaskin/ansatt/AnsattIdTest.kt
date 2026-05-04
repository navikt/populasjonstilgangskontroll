package no.nav.tilgangsmaskin.ansatt

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class AnsattIdTest : BehaviorSpec({
    Given("en AnsattId") {
        When("verdien er gyldig") {
            Then("opprettes uten feil") { AnsattId("A123456").verdi shouldBe "A123456" }
        }
        When("verdien har ugyldig lengde") {
            Then("kastes IllegalArgumentException") { shouldThrow<IllegalArgumentException> { AnsattId("A12345") } }
        }
        When("verdien ikke starter med bokstav") {
            Then("kastes IllegalArgumentException") { shouldThrow<IllegalArgumentException> { AnsattId("&123456") } }
        }
        When("verdien ikke slutter med 6 tall") {
            Then("kastes IllegalArgumentException") { shouldThrow<IllegalArgumentException> { AnsattId("A12345a") } }
        }
    }
})
