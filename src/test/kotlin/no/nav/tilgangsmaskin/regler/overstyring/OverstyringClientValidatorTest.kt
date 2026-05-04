package no.nav.tilgangsmaskin.regler.overstyring

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringClientValidator.OverstyringException
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.mock.env.MockEnvironment

class OverstyringClientValidatorTest : BehaviorSpec({

    val token = mockk<Token>()
    val cfg = OverstyringConfig(systemer = setOf("histark", "gosys"))

    fun validator(activeProfiles: List<String> = listOf("dev-gcp")) =
        OverstyringClientValidator(cfg, token, MockEnvironment().apply {
            setActiveProfiles(*activeProfiles.toTypedArray())
        })

    Given("validerKonsument - i prod") {
        When("system er godkjent (histark)") {
            Then("kastes ikke exception") {
                every { token.systemNavn } returns "histark"
                validator(listOf("prod-gcp")).validerKonsument()
            }
        }
        When("system er godkjent (gosys)") {
            Then("kastes ikke exception") {
                every { token.systemNavn } returns "gosys"
                validator(listOf("prod-gcp")).validerKonsument()
            }
        }
        When("system er ukjent") {
            Then("kastes OverstyringException med systemnavnet") {
                every { token.systemNavn } returns "ukjent-system"
                val ex = shouldThrow<OverstyringException> { validator(listOf("prod-gcp")).validerKonsument() }
                ex.system shouldBe "ukjent-system"
            }
        }
    }

    Given("validerKonsument - utenfor prod") {
        When("ukjent system i dev-gcp") {
            Then("kastes ikke exception") {
                every { token.systemNavn } returns "ukjent-system"
                validator(listOf("dev-gcp")).validerKonsument()
            }
        }
        When("ukjent system lokalt") {
            Then("kastes ikke exception") {
                every { token.systemNavn } returns "ukjent-system"
                validator(listOf("local")).validerKonsument()
            }
        }
    }
})
