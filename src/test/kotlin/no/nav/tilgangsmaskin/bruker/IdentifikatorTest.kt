package no.nav.tilgangsmaskin.bruker

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils

class BrukerIdTest : BehaviorSpec({

    Given("Identifikator") {
        When("verdien er en gyldig AktørId (13 siffer)") {
            Then("aksepteres den") { Identifikator("1234567890123") }
        }
        When("verdien er en gyldig BrukerId (11 siffer)") {
            Then("aksepteres den") { Identifikator("08526835671") }
        }
        When("verdien verken er gyldig AktørId eller BrukerId") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { Identifikator("abc") }
            }
        }
    }

    Given("AktørId") {
        When("aktørId har 13 siffer") {
            Then("aksepteres den") { AktørId("1234567890123") }
        }
        When("aktørId inneholder ikke-numeriske tegn") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { AktørId("123456789012a") }
            }
        }
        When("aktørId har feil lengde") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { AktørId("123456789") }
            }
        }
    }

    Given("BrukerId") {
        When("gyldig fødselsnummer") {
            Then("opprettes uten problemer") { BrukerId("08526835671") }
        }
        When("fødselsnummer har ugyldig lengde") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { BrukerId("111") }
            }
        }
        When("fødselsnummer inneholder ikke bare tall") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { BrukerId("1111111111a") }
            }
        }
    }

    Given("BrukerId i prod - mod11 grener") {
        beforeEach {
            mockkObject(ClusterUtils)
            every { ClusterUtils.isProd } returns true
        }
        afterEach { unmockkObject(ClusterUtils) }

        When("W1=0 og W2=0") {
            Then("begge kontrollsiffer er 0 og aksepteres") { BrukerId("00000000000") }
        }
        When("W1=else og W2=else") {
            Then("vanlig gyldig fødselsnummer aksepteres") { BrukerId("08526835671") }
        }
        When("W1=1") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { BrukerId("08526835682") }
            }
        }
        When("W2=1") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { BrukerId("10000000910") }
            }
        }
        When("W2 kontrollsiffer matcher ikke") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> { BrukerId("08526835672") }
            }
        }
    }
})
