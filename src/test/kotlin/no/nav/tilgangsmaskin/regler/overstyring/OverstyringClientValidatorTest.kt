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
    val cfg = OverstyringConfig(setOf("histark", "gosys"))

    fun validator(activeProfiles: List<String> = listOf("dev-gcp")) =
        OverstyringClientValidator(cfg, token, MockEnvironment().apply {
            setActiveProfiles(*activeProfiles.toTypedArray())
        })

    Given("validerKonsument i prod") {
        When("systemet er godkjent") {
            Then("kastes ingen exception") {
                every { token.systemNavn } returns "histark"
                validator(listOf("prod-gcp")).validerKonsument()
            }
        }

        When("systemet er gosys") {
            Then("kastes ingen exception") {
                every { token.systemNavn } returns "gosys"
                validator(listOf("prod-gcp")).validerKonsument()
            }
        }

        When("systemet er ukjent") {
            Then("kastes OverstyringException") {
                every { token.systemNavn } returns "ukjent-system"
                shouldThrow<OverstyringException> {
                    validator(listOf("prod-gcp")).validerKonsument()
                }
            }
            Then("exception inneholder systemnavnet") {
                every { token.systemNavn } returns "ukjent-system"
                val ex = shouldThrow<OverstyringException> {
                    validator(listOf("prod-gcp")).validerKonsument()
                }
                ex.system shouldBe "ukjent-system"
            }
        }
    }

    Given("validerKonsument utenfor prod") {
        When("systemet er ukjent i dev") {
            Then("kastes ingen exception") {
                every { token.systemNavn } returns "ukjent-system"
                validator(listOf("dev-gcp")).validerKonsument()
            }
        }

        When("systemet er ukjent lokalt") {
            Then("kastes ingen exception") {
                every { token.systemNavn } returns "ukjent-system"
                validator(listOf("local")).validerKonsument()
            }
        }
    }
})
