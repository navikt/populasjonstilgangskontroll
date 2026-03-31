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
        When("Instant er i fortiden") {
            Then("returnerer true") {
                Instant.now().minusSeconds(1).isBeforeNow() shouldBe true
            }
        }
        When("Instant er i fremtiden") {
            Then("returnerer false") {
                Instant.now().plusSeconds(60).isBeforeNow() shouldBe false
            }
        }
    }

    Given("toInstant") {
        When("LocalDate konverteres til Instant") {
            Then("er ved start av dagen og før nå") {
                val dato = LocalDate.of(2024, 6, 1)
                val instant = dato.toInstant()
                instant.isBefore(Instant.now()) shouldBe true
            }
        }
        When("to ulike datoer konverteres") {
            Then("gir ulike Instant-verdier") {
                val dato1 = LocalDate.of(2024, 1, 1)
                val dato2 = LocalDate.of(2024, 1, 2)
                dato1.toInstant().isBefore(dato2.toInstant()) shouldBe true
            }
        }
    }

    Given("månederSidenIdag") {
        When("dato er 12 hele måneder siden") {
            Then("gir 12") {
                idag.minusMonths(12).månederSidenIdag() shouldBe 12
            }
        }
        When("dato er 24 hele måneder siden") {
            Then("gir 24") {
                idag.minusMonths(24).månederSidenIdag() shouldBe 24
            }
        }
        When("dagOfMonth i dag er større enn i datoen") {
            Then("legger til 1 ekstra måned") {
                val dato = idag.minusMonths(1).minusDays(1)
                dato.månederSidenIdag() shouldBe 2
            }
        }
        When("dato er i fremtiden") {
            Then("kaster IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    idag.plusDays(1).månederSidenIdag()
                }
            }
        }
        When("dato er dagens dato") {
            Then("kaster IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    idag.månederSidenIdag()
                }
            }
        }
        When("dato er i fremtiden og feilen inspiseres") {
            Then("inneholder datoen og dagens dato i meldingen") {
                val fremtidigDato = idag.plusDays(1)
                val feil = shouldThrow<IllegalArgumentException> {
                    fremtidigDato.månederSidenIdag()
                }
                feil.message shouldBe "Datoen $fremtidigDato er ikke før dagens dato $idag"
            }
        }
    }

    Given("intervallSiden") {
        When("dato er 1 dag siden (0 måneder)") {
            Then("returnerer MND_0_6") {
                idag.minusDays(1).intervallSiden() shouldBe MND_0_6
            }
        }
        When("dato er 7 måneder siden") {
            Then("returnerer MND_7_12") {
                idag.minusMonths(7).intervallSiden() shouldBe MND_7_12
            }
        }
        When("dato er 12 måneder siden") {
            Then("returnerer MND_7_12") {
                idag.minusMonths(12).intervallSiden() shouldBe MND_7_12
            }
        }
        When("dato er 13 måneder siden") {
            Then("returnerer MND_13_24") {
                idag.minusMonths(13).intervallSiden() shouldBe MND_13_24
            }
        }
        When("dato er 24 måneder siden") {
            Then("returnerer MND_13_24") {
                idag.minusMonths(24).intervallSiden() shouldBe MND_13_24
            }
        }
        When("dato er 25 måneder siden") {
            Then("returnerer MND_OVER_24") {
                idag.minusMonths(25).intervallSiden() shouldBe MND_OVER_24
            }
        }
        When("dato er 10 år siden") {
            Then("returnerer MND_OVER_24") {
                idag.minusYears(10).intervallSiden() shouldBe MND_OVER_24
            }
        }
    }


    Given("diffFromNow") {
        When("Instant er i fremtiden") {
            Then("returnerer en ikke-tom streng med differansen") {
                val fremtid = Instant.now().plusSeconds(90)
                val diff = fremtid.diffFromNow()
                diff.isNotBlank() shouldBe true
            }
        }
        When("Instant er i fortiden") {
            Then("returnerer tom streng fordi alle verdier er 0 eller negative") {
                val fortid = Instant.now().minusSeconds(1)
                fortid.diffFromNow() shouldBe ""
            }
        }
    }
})

