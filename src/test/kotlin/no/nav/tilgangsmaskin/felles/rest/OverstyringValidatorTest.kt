package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import jakarta.validation.ConstraintValidatorContext
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringData
import java.time.LocalDate

class OverstyringValidatorTest : DescribeSpec({

    val ctx = mockk<ConstraintValidatorContext>(relaxed = true)
    val validator = OverstyringValidator()

    val gyldigBegrunnelse = "En gyldig begrunnelse på minst 10 tegn"
    val gyldigDato = LocalDate.now().plusMonths(1)
    val brukerId = BrukerId("08526835670")

    fun data(begrunnelse: String = gyldigBegrunnelse, gyldigtil: LocalDate = gyldigDato) =
        OverstyringData(brukerId, begrunnelse, gyldigtil)

    describe("isValid") {

        describe("dato") {

            it("gyldig dato én dag frem i tid er gyldig") {
                validator.isValid(data(gyldigtil = LocalDate.now().plusDays(1)), ctx) shouldBe true
            }

            it("gyldig dato én måned frem i tid er gyldig") {
                validator.isValid(data(gyldigtil = LocalDate.now().plusMonths(1)), ctx) shouldBe true
            }

            it("dato akkurat 3 måneder frem i tid er ugyldig") {
                validator.isValid(data(gyldigtil = LocalDate.now().plusMonths(3)), ctx) shouldBe false
            }

            it("dato mer enn 3 måneder frem i tid er ugyldig") {
                validator.isValid(data(gyldigtil = LocalDate.now().plusMonths(4)), ctx) shouldBe false
            }

            it("dagens dato er ugyldig") {
                validator.isValid(data(gyldigtil = LocalDate.now()), ctx) shouldBe false
            }

            it("dato i fortiden er ugyldig") {
                validator.isValid(data(gyldigtil = LocalDate.now().minusDays(1)), ctx) shouldBe false
            }
        }

        describe("begrunnelse") {

            it("begrunnelse på nøyaktig 10 tegn er gyldig") {
                validator.isValid(data(begrunnelse = "1234567890"), ctx) shouldBe true
            }

            it("begrunnelse på nøyaktig 400 tegn er gyldig") {
                validator.isValid(data(begrunnelse = "a".repeat(400)), ctx) shouldBe true
            }

            it("begrunnelse på 9 tegn er ugyldig") {
                validator.isValid(data(begrunnelse = "123456789"), ctx) shouldBe false
            }

            it("begrunnelse på 401 tegn er ugyldig") {
                validator.isValid(data(begrunnelse = "a".repeat(401)), ctx) shouldBe false
            }

            it("tom begrunnelse er ugyldig") {
                validator.isValid(data(begrunnelse = ""), ctx) shouldBe false
            }
        }

        describe("kombinasjoner") {

            it("ugyldig dato og ugyldig begrunnelse er ugyldig") {
                validator.isValid(data(begrunnelse = "kort", gyldigtil = LocalDate.now().minusDays(1)), ctx) shouldBe false
            }

            it("gyldig dato og gyldig begrunnelse er gyldig") {
                validator.isValid(data(begrunnelse = gyldigBegrunnelse, gyldigtil = gyldigDato), ctx) shouldBe true
            }
        }
    }
})
