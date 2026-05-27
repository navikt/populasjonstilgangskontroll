package no.nav.tilgangsmaskin.felles.utils.extensions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_0_6
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_13_24
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_7_12
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_OVER_24
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.diffFromNow
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.format
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.isBeforeNow
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.isBetween
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.local
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.månederSidenIdag
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.toInstant
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TimeExtensionsTest : BehaviorSpec({

    val fastDato = LocalDate.of(2026, 5, 27)
    val OSLO = ZoneId.of("Europe/Oslo")
    val fastClock = Clock.fixed(fastDato.atStartOfDay(OSLO).toInstant(), OSLO)

    Given("isBeforeNow med fast Clock") {
        When("Instant er før klokken") {
            Then("returneres true") {
                Instant.parse("2020-01-01T00:00:00Z").isBeforeNow(fastClock).shouldBeTrue()
            }
        }
        When("Instant er etter klokken") {
            Then("returneres false") {
                Instant.parse("2030-01-01T00:00:00Z").isBeforeNow(fastClock) shouldBe false
            }
        }
    }

    Given("toInstant") {
        When("LocalDate konverteres med eksplisitt sone") {
            Then("gir midnatt i den sonen (Oslo sommertid = UTC+2)") {
                LocalDate.of(2024, 6, 1).toInstant(OSLO) shouldBe Instant.parse("2024-05-31T22:00:00Z")
            }
        }
    }

    Given("månederSidenIdag med fast Clock") {
        When("12 måneder før klokken") {
            Then("gir 12") {
                LocalDate.of(2025, 5, 27).månederSidenIdag(fastClock) shouldBe 12
            }
        }
        When("24 måneder før klokken") {
            Then("gir 24") {
                LocalDate.of(2024, 5, 27).månederSidenIdag(fastClock) shouldBe 24
            }
        }
        When("dato er lik klokkens dato") {
            Then("gir 1") {
                fastDato.månederSidenIdag(fastClock) shouldBe 1
            }
        }
        When("dato er etter klokkens dato") {
            Then("kastes IllegalArgumentException med riktig melding") {
                val fremtid = LocalDate.of(2027, 1, 1)
                val feil = shouldThrow<IllegalArgumentException> { fremtid.månederSidenIdag(fastClock) }
                feil.message shouldBe "Datoen $fremtid er etter dagens dato $fastDato"
            }
        }
    }

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

    Given("diffFromNow med fast Clock") {
        When("Instant er etter klokken") {
            Then("gir ikke-tom streng") {
                Instant.parse("2026-05-27T01:30:00Z").diffFromNow(fastClock).isNotBlank().shouldBeTrue()
            }
        }
        When("Instant er før klokken") {
            Then("gir tom streng") {
                Instant.parse("2020-01-01T00:00:00Z").diffFromNow(fastClock) shouldBe ""
            }
        }
    }

    Given("Duration.format") {
        When("alle komponenter > 1") {
            Then("flertallsformer med enkle mellomrom") {
                (2.hours + 3.minutes + 4.seconds).format() shouldBe "2 timer 3 minutter 4 sekunder"
            }
        }
        When("alle komponenter = 1") {
            Then("entallsformer") {
                (1.hours + 1.minutes + 1.seconds).format() shouldBe "1 time 1 minutt 1 sekund"
            }
        }
        When("dager inkludert") {
            Then("dag-komponent først") {
                (25.hours + 30.minutes).format() shouldBe "1 dag 1 time 30 minutter"
            }
        }
        When("ingen komponenter > 0") {
            Then("tom streng") {
                0.seconds.format() shouldBe ""
            }
        }
    }

    Given("månederSidenIdag avrunding") {
        When("dato er én dag før klokkens dag-i-måned (rundes opp)") {
            Then("1 år + 1 mnd siden gir 13") {
                LocalDate.of(2025, 4, 26).månederSidenIdag(fastClock) shouldBe 14
            }
        }
        When("dato er nøyaktig på samme dag-i-måned") {
            Then("12 mnd siden gir 12") {
                LocalDate.of(2025, 5, 27).månederSidenIdag(fastClock) shouldBe 12
            }
        }
    }

    Given("isBetween") {
        val start = LocalDate.of(2026, 1, 1)
        val end = LocalDate.of(2026, 12, 31)
        When("dato er innenfor") {
            Then("returnerer true") {
                LocalDate.of(2026, 6, 15).isBetween(start, end).shouldBeTrue()
            }
        }
        When("dato er lik start") {
            Then("returnerer true (inklusivt)") {
                start.isBetween(start, end).shouldBeTrue()
            }
        }
        When("dato er lik end") {
            Then("returnerer true (inklusivt)") {
                end.isBetween(start, end).shouldBeTrue()
            }
        }
        When("dato er før start") {
            Then("returnerer false") {
                LocalDate.of(2025, 12, 31).isBetween(start, end) shouldBe false
            }
        }
        When("dato er etter end") {
            Then("returnerer false") {
                LocalDate.of(2027, 1, 1).isBetween(start, end) shouldBe false
            }
        }
    }

    Given("Long.local") {
        When("epoch-millis konverteres med default-format") {
            Then("formaterer i Europe/Oslo-tid") {
                // 2026-05-27T10:00:00Z = 12:00:00 i Oslo (sommertid UTC+2)
                Instant.parse("2026-05-27T10:00:00Z").toEpochMilli().local() shouldBe "2026-05-27 12:00:00"
            }
        }
        When("egendefinert format brukes") {
            Then("respekterer formatet") {
                Instant.parse("2026-05-27T10:00:00Z").toEpochMilli().local("yyyy-MM-dd") shouldBe "2026-05-27"
            }
        }
    }
})
