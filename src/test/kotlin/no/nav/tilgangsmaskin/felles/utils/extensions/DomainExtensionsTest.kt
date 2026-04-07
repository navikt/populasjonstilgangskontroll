package no.nav.tilgangsmaskin.felles.utils.extensions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.requireDigits
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.upcase

class DomainExtensionsTest : DescribeSpec({

    describe("requireDigits") {

        it("godtar streng med kun siffer og riktig lengde") {
            requireDigits("08526835670", 11)
        }

        it("kaster ved bokstav i strengen") {
            shouldThrow<IllegalArgumentException> {
                requireDigits("0852683567a", 11)
            }
        }

        it("kaster ved spesialtegn i strengen") {
            shouldThrow<IllegalArgumentException> {
                requireDigits("0852683567-", 11)
            }
        }

        it("kaster ved for kort streng") {
            shouldThrow<IllegalArgumentException> {
                requireDigits("0852683567", 11)
            }
        }

        it("kaster ved for lang streng") {
            shouldThrow<IllegalArgumentException> {
                requireDigits("085268356701", 11)
            }
        }

        it("kaster ved tom streng") {
            shouldThrow<IllegalArgumentException> {
                requireDigits("", 11)
            }
        }

        it("godtar 13-sifret aktørId") {
            requireDigits("1234567890123", 13)
        }
    }

    describe("maskFnr") {

        it("maskerer 11-sifret fnr fra posisjon 4") {
            "08526835670".maskFnr() shouldBe "0852*******"
        }

        it("maskerer 13-sifret aktørId fra posisjon 6") {
            "1234567890123".maskFnr() shouldBe "123456*******"
        }

        it("returnerer kort streng uendret") {
            "Z999999".maskFnr() shouldBe "Z999999"
        }

        it("returnerer tom streng uendret") {
            "".maskFnr() shouldBe ""
        }

        it("returnerer streng med annen lengde uendret") {
            "12345".maskFnr() shouldBe "12345"
        }

        it("maskert fnr har alltid 11 tegn totalt") {
            "08526835670".maskFnr().length shouldBe 11
        }

        it("maskert aktørId har alltid 13 tegn totalt") {
            "1234567890123".maskFnr().length shouldBe 13
        }
    }

    describe("upcase") {

        it("gjør første bokstav stor") {
            "hello".upcase() shouldBe "Hello"
        }

        it("endrer ikke resten av strengen") {
            "hELLO".upcase() shouldBe "HELLO"
        }

        it("håndterer allerede stor forbokstav") {
            "Hello".upcase() shouldBe "Hello"
        }

        it("håndterer tom streng") {
            "".upcase() shouldBe ""
        }
    }
})

