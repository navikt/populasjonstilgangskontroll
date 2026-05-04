package no.nav.tilgangsmaskin.felles.utils.extensions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_0_6
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_13_24
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_7_12
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_OVER_24
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.diffFromNow
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.isBeforeNow
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.månederSidenIdag
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.toInstant
import java.time.Instant
import java.time.LocalDate

class TimeExtensionsTest : BehaviorSpec({

    val idag = LocalDate.now()

    Given("isBeforeNow") {
        When("Instant er i fortiden") { Then("returneres true")  { Instant.now().minusSeconds(1).isBeforeNow() shouldBe true  } }
        When("Instant er i fremtiden") { Then("returneres false") { Instant.now().plusSeconds(60).isBeforeNow() shouldBe false } }
    }

    Given("toInstant") {
        When("LocalDate konverteres") {
            Then("gir Instant ved start av dagen") {
                LocalDate.of(2024, 6, 1).toInstant().isBefore(Instant.now()) shouldBe true
            }
        }
        When("to ulike datoer konverteres") {
            Then("gir ulike Instant-verdier") {
                LocalDate.of(2024, 1, 1).toInstant().isBefore(LocalDate.of(2024, 1, 2).toInstant()) shouldBe true
            }
        }
    }

    Given("månederSidenIdag") {
        xwhen("6 hele måneder siden") { Then("gir 6") { idag.minusMonths(6).månederSidenIdag() shouldBe 6 } }
        When("12 hele måneder siden") { Then("gir 12") { idag.minusMonths(12).månederSidenIdag() shouldBe 12 } }
        When("24 hele måneder siden") { Then("gir 24") { idag.minusMonths(24).månederSidenIdag() shouldBe 24 } }
        When("dato er i fremtiden") { Then("kastes IllegalArgumentException med riktig melding") {
            val fremtidigDato = idag.plusDays(1)
            val feil = shouldThrow<IllegalArgumentException> { fremtidigDato.månederSidenIdag() }
            feil.message shouldBe "Datoen $fremtidigDato er etter dagens dato $idag"
        } }
        When("dato er dagens dato") { Then("gir 1") { idag.månederSidenIdag() shouldBe 1 } }
    }

    Given("intervallSiden") {
        When("1 dag siden") { Then("gir MND_0_6") { idag.minusDays(1).intervallSiden() shouldBe MND_0_6 } }
        xwhen("6 måneder siden") { Then("gir MND_0_6") { idag.minusMonths(6).intervallSiden() shouldBe MND_0_6 } }
        When("7 måneder siden") { Then("gir MND_7_12")  { idag.minusMonths(7).intervallSiden() shouldBe MND_7_12  } }
        When("12 måneder siden") { Then("gir MND_7_12") { idag.minusMonths(12).intervallSiden() shouldBe MND_7_12 } }
        When("13 måneder siden") { Then("gir MND_13_24") { idag.minusMonths(13).intervallSiden() shouldBe MND_13_24 } }
        When("24 måneder siden") { Then("gir MND_13_24") { idag.minusMonths(24).intervallSiden() shouldBe MND_13_24 } }
        When("25 måneder siden") { Then("gir MND_OVER_24") { idag.minusMonths(25).intervallSiden() shouldBe MND_OVER_24 } }
        When("10 år siden")      { Then("gir MND_OVER_24") { idag.minusYears(10).intervallSiden() shouldBe MND_OVER_24 } }
    }

    Given("Dødsperiode.tekst") {
        When("MND_0_6")   { Then("tekst er 0-6")  { MND_0_6.tekst shouldBe "0-6"   } }
        When("MND_7_12")  { Then("tekst er 7-12") { MND_7_12.tekst shouldBe "7-12"  } }
        When("MND_13_24") { Then("tekst er 13-24") { MND_13_24.tekst shouldBe "13-24" } }
        When("MND_OVER_24") { Then("tekst er >24") { MND_OVER_24.tekst shouldBe ">24"  } }
    }

    Given("diffFromNow") {
        When("Instant er i fremtiden") { Then("gir ikke-tom streng") {
            Instant.now().plusSeconds(90).diffFromNow().isNotBlank() shouldBe true
        } }
        When("Instant er i fortiden") { Then("gir tom streng") {
            Instant.now().minusSeconds(1).diffFromNow() shouldBe ""
        } }
    }
})

