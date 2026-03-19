package no.nav.tilgangsmaskin.regler.overstyring

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringClientValidator.OverstyringException
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.mock.env.MockEnvironment

class OverstyringClientValidatorTest : DescribeSpec({

    val token = mockk<Token>()
    val cfg = OverstyringConfig(systemer = setOf("histark", "gosys"))

    fun validator(activeProfiles: List<String> = listOf("dev-gcp")) =
        OverstyringClientValidator(cfg, token, MockEnvironment().apply {
            setActiveProfiles(*activeProfiles.toTypedArray())
        })

    describe("validerKonsument") {

        describe("i prod") {

            it("kaster ikke for godkjent system") {
                every { token.systemNavn } returns "histark"
                validator(listOf("prod-gcp")).validerKonsument()
            }

            it("kaster for ukjent system") {
                every { token.systemNavn } returns "ukjent-system"
                shouldThrow<OverstyringException> {
                    validator(listOf("prod-gcp")).validerKonsument()
                }
            }

            it("exception inneholder systemnavnet") {
                every { token.systemNavn } returns "ukjent-system"
                val ex = shouldThrow<OverstyringException> {
                    validator(listOf("prod-gcp")).validerKonsument()
                }
                ex.system shouldBe "ukjent-system"
            }

            it("kaster ikke for gosys") {
                every { token.systemNavn } returns "gosys"
                validator(listOf("prod-gcp")).validerKonsument()
            }
        }

        describe("utenfor prod") {

            it("kaster ikke for ukjent system i dev") {
                every { token.systemNavn } returns "ukjent-system"
                validator(listOf("dev-gcp")).validerKonsument()
            }

            it("kaster ikke for ukjent system lokalt") {
                every { token.systemNavn } returns "ukjent-system"
                validator(listOf("local")).validerKonsument()
            }
        }
    }
})
