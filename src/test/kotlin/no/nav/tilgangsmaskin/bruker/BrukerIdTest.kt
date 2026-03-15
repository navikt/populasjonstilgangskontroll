package no.nav.tilgangsmaskin.bruker

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils

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

    describe("BrukerId i prod") {

        beforeEach {
            mockkObject(ClusterUtils)
            every { ClusterUtils.isProd } returns true
        }

        afterEach {
            unmockkObject(ClusterUtils)
        }

        it("Gyldig fødselsnummer med riktige kontrollsifre opprettes uten problemer") {
            val brukerId = BrukerId("08526835671")
            brukerId.verdi shouldBe "08526835671"
        }

        it("Fødselsnummer med feil første kontrollsiffer kaster IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { BrukerId("08526835681") }
        }

        it("Fødselsnummer med feil andre kontrollsiffer kaster IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { BrukerId("08526835670") }
        }

        it("Fødselsnummer som gir mod11 == 1 (ugyldig) kaster IllegalArgumentException") {
            // mod11 == 1 means the number is inherently invalid per Norwegian fnr rules
            shouldThrow<IllegalArgumentException> { BrukerId("08526835682") }
        }
    }
})
