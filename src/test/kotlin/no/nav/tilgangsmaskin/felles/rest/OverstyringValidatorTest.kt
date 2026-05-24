package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import jakarta.validation.ConstraintValidatorContext
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringValidator
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringData
import java.time.LocalDate
import java.time.LocalDate.now

class OverstyringValidatorTest : BehaviorSpec({

    val ctx = mockk<ConstraintValidatorContext>(relaxed = true)
    val validator = OverstyringValidator()

    val gyldigBegrunnelse = "En gyldig begrunnelse på minst 10 tegn"
    val gyldigDato = now().plusMonths(1)
    val brukerId = BrukerId("08526835670")

    fun data(begrunnelse: String, gyldigtil: LocalDate = gyldigDato) =
        OverstyringData(brukerId, begrunnelse, gyldigtil)

    Given("en overstyring") {

        When("dato er én dag frem i tid") {
            Then("er gyldig") {
                validator.isValid(data(gyldigBegrunnelse, now().plusDays(1)), ctx).shouldBeTrue()
            }
        }

        When("dato er én måned frem i tid") {
            Then("er gyldig") {
                validator.isValid(data(gyldigBegrunnelse, now().plusMonths(1)), ctx).shouldBeTrue()
            }
        }

        When("dato er akkurat 3 måneder frem i tid") {
            Then("er ugyldig") {
                validator.isValid(data(gyldigBegrunnelse, now().plusMonths(3)), ctx).shouldBeTrue()
            }
        }

        When("dato er mer enn 3 måneder frem i tid") {
            Then("er ugyldig") {
                validator.isValid(data(gyldigBegrunnelse, now().plusMonths(4)), ctx) shouldBe false
            }
        }

        When("dato er dagens dato") {
            Then("gyldig") {
                validator.isValid(data(gyldigBegrunnelse, now()), ctx).shouldBeTrue()
            }
        }

        When("dato er i fortiden") {
            Then("er ugyldig") {
                validator.isValid(data(gyldigBegrunnelse, now().minusDays(1)), ctx) shouldBe false
            }
        }

        When("begrunnelse er på nøyaktig 10 tegn") {
            Then("er gyldig") {
                validator.isValid(data("1234567890"), ctx).shouldBeTrue()
            }
        }

        When("begrunnelse er på nøyaktig 255 tegn") {
            Then("er gyldig") {
                validator.isValid(data("a".repeat(255)), ctx).shouldBeTrue()
            }
        }

        When("begrunnelse er på 9 tegn") {
            Then("er ugyldig") {
                validator.isValid(data("123456789"), ctx) shouldBe false
            }
        }

        When("begrunnelse er på 401 tegn") {
            Then("er ugyldig") {
                validator.isValid(data("a".repeat(401)), ctx) shouldBe false
            }
        }

        When("begrunnelse er tom") {
            Then("er ugyldig") {
                validator.isValid(data( ""), ctx) shouldBe false
            }
        }

        When("dato er ugyldig og begrunnelse er ugyldig") {
            Then("er ugyldig") {
                validator.isValid(data(begrunnelse = "kort", gyldigtil = now().minusDays(1)), ctx) shouldBe false
            }
        }

        When("dato er gyldig og begrunnelse er gyldig") {
            Then("er gyldig") {
                validator.isValid(data(begrunnelse = gyldigBegrunnelse, gyldigtil = gyldigDato), ctx).shouldBeTrue()
            }
        }
    }
})
