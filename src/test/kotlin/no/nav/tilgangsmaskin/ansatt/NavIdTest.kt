package no.nav.tilgangsmaskin.ansatt

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class NavIdTest : DescribeSpec({
    describe("AnsattId") {
        it("Gyldig ansattId OK") {
            AnsattId("A123456").verdi shouldBe "A123456"
        }
        it("ansattId med ugyldig lengde skal kaste IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { AnsattId("A12345") }
        }
        it("ansattId uten bokstav først skal kaste IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { AnsattId("&123456") }
        }
        it("ansattId uten 6 tall etter første bokstav skal kaste IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { AnsattId("A12345a") }
        }
    }
})
