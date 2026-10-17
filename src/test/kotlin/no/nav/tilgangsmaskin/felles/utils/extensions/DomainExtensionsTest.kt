package no.nav.tilgangsmaskin.felles.utils.extensions

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.sikkerhetstjenesten.felles.utils.extensions.TimeExtensions.OSLO
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.Dødsperiode.MND_0_6
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.Dødsperiode.MND_13_24
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.Dødsperiode.MND_7_12
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.Dødsperiode.MND_OVER_24
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.intervallSiden
import java.time.Clock
import java.time.LocalDate

class DomainExtensionsTest : BehaviorSpec({
    val fastDato = LocalDate.of(2026, 5, 27)
    val fastClock = Clock.fixed(fastDato.atStartOfDay(OSLO).toInstant(), OSLO)

    Given("intervallSiden med fast Clock") {
        When("3 måneder før klokken") {
            Then("gir MND_0_6") {
                LocalDate.of(2026, 2, 27).intervallSiden(fastClock) shouldBe MND_0_6
            }
        }
        When("12 måneder før klokken") {
            Then("gir MND_7_12 (grense)") {
                LocalDate.of(2025, 5, 27).intervallSiden(fastClock) shouldBe MND_7_12
            }
        }
        When("18 måneder før klokken") {
            Then("gir MND_13_24") {
                LocalDate.of(2024, 11, 27).intervallSiden(fastClock) shouldBe MND_13_24
            }
        }
        When("24 måneder før klokken") {
            Then("gir MND_13_24 (grense)") {
                LocalDate.of(2024, 5, 27).intervallSiden(fastClock) shouldBe MND_13_24
            }
        }
        When("3 år før klokken") {
            Then("gir MND_OVER_24") {
                LocalDate.of(2023, 5, 27).intervallSiden(fastClock) shouldBe MND_OVER_24
            }
        }
    }
})
