package no.nav.tilgangsmaskin.bruker

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils

class BrukerIdTest : DescribeSpec({

    describe("Identifikator") {

        it("gyldig AktørId (13 siffer) aksepteres") {
            Identifikator("1234567890123")
        }

        it("gyldig BrukerId (11 siffer) aksepteres når AktørId feiler") {
            Identifikator("08526835671")
        }

        it("verdi som verken er gyldig AktørId eller BrukerId kaster IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { Identifikator("abc") }
        }
    }

    describe("AktørId") {

        it("gyldig aktørId med 13 siffer aksepteres") {
            AktørId("1234567890123")
        }

        it("aktørId med ikke-numeriske tegn kaster IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { AktørId("123456789012a") }
        }

        it("aktørId med feil lengde kaster IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { AktørId("123456789") }
        }
    }

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

    describe("BrukerId i prod - mod11 grener") {

        beforeEach {
            mockkObject(ClusterUtils)
            every { ClusterUtils.isProd } returns true
        }

        afterEach {
            unmockkObject(ClusterUtils)
        }

        it("W1=0 og W2=0: begge kontrollsiffer er 0 (00000000000)") {
            BrukerId("00000000000")
        }

        it("W1=else og W2=else: vanlig gyldig fødselsnummer (08526835671)") {
            BrukerId("08526835671")
        }

        it("W1=1: kaster IllegalArgumentException (08526835682)") {
            shouldThrow<IllegalArgumentException> { BrukerId("08526835682") }
        }

        it("W2=1: kaster IllegalArgumentException (10000000910)") {
            shouldThrow<IllegalArgumentException> { BrukerId("10000000910") }
        }

        it("W2 kontrollsiffer matcher ikke: kaster IllegalArgumentException (08526835672)") {
            shouldThrow<IllegalArgumentException> { BrukerId("08526835672") }
        }
    }
})
