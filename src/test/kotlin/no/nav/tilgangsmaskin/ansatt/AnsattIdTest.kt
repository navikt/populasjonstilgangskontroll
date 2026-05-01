package no.nav.tilgangsmaskin.ansatt

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class AnsattIdTest : BehaviorSpec({
    Given("en AnsattId") {
        When("format er gyldig") {
            Then("opprettes uten feil") {
                AnsattId("A123456").verdi shouldBe "A123456"
            }
        }
        When("lengden er ugyldig") {
            Then("kaster IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { AnsattId("A12345") }
            }
        }
        When("første tegn ikke er en bokstav") {
            Then("kaster IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { AnsattId("&123456") }
            }
        }
        When("tegnene etter bokstaven ikke er 6 siffer") {
            Then("kaster IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { AnsattId("A12345a") }
            }
        }
    }
})
