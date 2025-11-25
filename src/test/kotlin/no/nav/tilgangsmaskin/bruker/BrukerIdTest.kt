package no.nav.tilgangsmaskin.bruker

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec

class BrukerIdTest : DescribeSpec({
    describe("BrukerId") {
        it("Gyldig Fødselsnummer skal opprettes uten problemer") {
            BrukerId("08526835671")
        }
        it("Fødselsnummer med ugyldig lengde skal kaste IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { BrukerId("111") }
        }
        it("Fødselsnummer uten bare tall skal kaste IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { BrukerId("1111111111a") }
        }
    }
})
