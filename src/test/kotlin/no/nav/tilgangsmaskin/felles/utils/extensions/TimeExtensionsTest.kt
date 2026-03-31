package no.nav.tilgangsmaskin.felles.utils.extensions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
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

class TimeExtensionsTest : DescribeSpec({

    val idag = LocalDate.now()

    describe("isBeforeNow") {

        it("returnerer true for Instant i fortiden") {
            Instant.now().minusSeconds(1).isBeforeNow() shouldBe true
        }

        it("returnerer false for Instant i fremtiden") {
            Instant.now().plusSeconds(60).isBeforeNow() shouldBe false
        }
    }

    describe("toInstant") {

        it("konverterer LocalDate til Instant ved start av dagen") {
            val dato = LocalDate.of(2024, 6, 1)
            val instant = dato.toInstant()
            instant.isBefore(Instant.now()) shouldBe true
        }

        it("to ulike datoer gir ulike Instant-verdier") {
            val dato1 = LocalDate.of(2024, 1, 1)
            val dato2 = LocalDate.of(2024, 1, 2)
            dato1.toInstant().isBefore(dato2.toInstant()) shouldBe true
        }
    }

    describe("månederSidenIdag") {


        xit("6 hele måneder siden gir 6") {
            idag.minusMonths(6).månederSidenIdag() shouldBe 6
        }

        it("12 hele måneder siden gir 12") {
            idag.minusMonths(12).månederSidenIdag() shouldBe 12
        }

        it("24 hele måneder siden gir 24") {
            idag.minusMonths(24).månederSidenIdag() shouldBe 24
        }

        it("legger til 1 ekstra måned når dagOfMonth i dag er større enn i datoen") {
            // idag er den 14., dato er 1 måned tilbake men med en dag tidligere (13.)
            val dato = idag.minusMonths(1).minusDays(1)
            dato.månederSidenIdag() shouldBe 2
        }

        it("kaster IllegalArgumentException for dato i fremtiden") {
            shouldThrow<IllegalArgumentException> {
                idag.plusDays(1).månederSidenIdag()
            }
        }

        it("kaster IllegalArgumentException for dagens dato") {
            shouldThrow<IllegalArgumentException> {
                idag.månederSidenIdag()
            }
        }

        it("IllegalArgumentException-meldingen inneholder datoen og dagens dato") {
            val fremtidigDato = idag.plusDays(1)
            val feil = shouldThrow<IllegalArgumentException> {
                fremtidigDato.månederSidenIdag()
            }
            feil.message shouldBe "Datoen $fremtidigDato er ikke før dagens dato $idag"
        }
    }

    describe("intervallSiden") {

        it("nedre grense: 1 dag siden (0 måneder)") {
            idag.minusDays(1).intervallSiden() shouldBe MND_0_6
        }
        xit("øvre grense: 6 måneder siden") {
            idag.minusMonths(6).intervallSiden() shouldBe MND_0_6
        }

        it("nedre grense: 7 måneder siden") {
            idag.minusMonths(7).intervallSiden() shouldBe MND_7_12
        }
        it("øvre grense: 12 måneder siden") {
            idag.minusMonths(12).intervallSiden() shouldBe MND_7_12
        }

        it("nedre grense: 13 måneder siden") {
            idag.minusMonths(13).intervallSiden() shouldBe MND_13_24
        }
        it("øvre grense: 24 måneder siden") {
            idag.minusMonths(24).intervallSiden() shouldBe MND_13_24
        }

        it("25 måneder siden") {
            idag.minusMonths(25).intervallSiden() shouldBe MND_OVER_24
        }
        it("10 år siden") {
            idag.minusYears(10).intervallSiden() shouldBe MND_OVER_24
        }
    }

    describe("Dødsperiode.tekst") {

        it("MND_0_6 har tekst 0-6") {
            MND_0_6.tekst shouldBe "0-6"
        }

        it("MND_7_12 har tekst 7-12") {
            MND_7_12.tekst shouldBe "7-12"
        }

        it("MND_13_24 har tekst 13-24") {
            MND_13_24.tekst shouldBe "13-24"
        }

        it("MND_OVER_24 har tekst >24") {
            MND_OVER_24.tekst shouldBe ">24"
        }
    }

    describe("diffFromNow") {

        it("positiv differanse for fremtidig Instant inneholder sekunder") {
            val fremtid = Instant.now().plusSeconds(90)
            val diff = fremtid.diffFromNow()
            diff.isNotBlank() shouldBe true
        }

        it("negativ differanse for fortid gir tom streng (alt er 0 eller negativt)") {
            val fortid = Instant.now().minusSeconds(1)
            // diffFromNow bruker between(now, this) — negativ duration gir 0-verdier
            fortid.diffFromNow() shouldBe ""
        }
    }
})

