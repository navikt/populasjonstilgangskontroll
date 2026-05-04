package no.nav.tilgangsmaskin.bruker

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils

class BrukerIdTest : BehaviorSpec({

    Given("Identifikator") {
        When("verdien er gyldig AktørId (13 siffer)") { Then("aksepteres") { Identifikator("1234567890123") } }
        When("verdien er gyldig BrukerId (11 siffer)") { Then("aksepteres") { Identifikator("08526835671") } }
        When("verdien er verken AktørId eller BrukerId") {
            Then("kastes IllegalArgumentException") { shouldThrow<IllegalArgumentException> { Identifikator("abc") } }
        }
    }

    Given("AktørId") {
        When("verdien er 13 siffer") { Then("aksepteres") { AktørId("1234567890123") } }
        When("verdien inneholder ikke-numeriske tegn") {
            Then("kastes IllegalArgumentException") { shouldThrow<IllegalArgumentException> { AktørId("123456789012a") } }
        }
        When("verdien har feil lengde") {
            Then("kastes IllegalArgumentException") { shouldThrow<IllegalArgumentException> { AktørId("123456789") } }
        }
    }

    Given("BrukerId") {
        When("verdien er gyldig fødselsnummer") { Then("opprettes uten feil") { BrukerId("08526835671") } }
        When("verdien har ugyldig lengde") {
            Then("kastes IllegalArgumentException") { shouldThrow<IllegalArgumentException> { BrukerId("111") } }
        }
        When("verdien inneholder ikke bare tall") {
            Then("kastes IllegalArgumentException") { shouldThrow<IllegalArgumentException> { BrukerId("1111111111a") } }
        }
    }

    Given("BrukerId i prod - mod11") {
        beforeEach {
            mockkObject(ClusterUtils)
            every { ClusterUtils.isProd } returns true
        }
        afterEach { unmockkObject(ClusterUtils) }

        When("W1=0 og W2=0") { Then("aksepteres (00000000000)") { BrukerId("00000000000") } }
        When("vanlig gyldig fødselsnummer") { Then("aksepteres (08526835671)") { BrukerId("08526835671") } }
        When("W1=1") {
            Then("kastes IllegalArgumentException (08526835682)") { shouldThrow<IllegalArgumentException> { BrukerId("08526835682") } }
        }
        When("W2=1") {
            Then("kastes IllegalArgumentException (10000000910)") { shouldThrow<IllegalArgumentException> { BrukerId("10000000910") } }
        }
        When("W2 kontrollsiffer matcher ikke") {
            Then("kastes IllegalArgumentException (08526835672)") { shouldThrow<IllegalArgumentException> { BrukerId("08526835672") } }
        }
    }
})
