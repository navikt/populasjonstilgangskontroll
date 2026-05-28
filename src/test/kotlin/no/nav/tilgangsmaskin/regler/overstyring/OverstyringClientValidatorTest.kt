package no.nav.tilgangsmaskin.regler.overstyring

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV_GCP
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD_GCP
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.LOCAL
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringClientValidator.OverstyringException
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.mock.env.MockEnvironment

class OverstyringClientValidatorTest : BehaviorSpec({

    val token = mockk<Token>()
    val cfg = OverstyringConfig()

    fun validator(activeProfiles: List<String> = listOf(DEV_GCP)) =
        OverstyringClientValidator(cfg, token, MockEnvironment().apply {
            setActiveProfiles(*activeProfiles.toTypedArray())
        })

    Given("validerKonsument - i prod") {
        When("system er godkjent (histark)") {
            Then("kastes ikke exception") {
                every { token.systemNavn } returns "histark"
                shouldNotThrowAny {
                    validator(listOf(PROD_GCP)).valider()
                }
            }
        }
        When("system er godkjent (gosys)") {
            Then("kastes ikke exception") {
                every { token.systemNavn } returns "gosys"
                shouldNotThrowAny {
                    validator(listOf(PROD_GCP)).valider()
                }
            }
        }
        When("system er ukjent") {
            Then("kastes OverstyringException med systemnavnet") {
                every { token.systemNavn } returns "ukjent-system"
                shouldThrow<OverstyringException> {
                    validator(listOf(PROD_GCP)).valider()
                }
            }
        }
    }

    Given("validerKonsument - utenfor prod") {
        When("ukjent system i dev-gcp") {
            Then("kastes ikke exception") {
                every { token.systemNavn } returns "ukjent-system"
                shouldNotThrowAny {
                    validator(listOf(DEV_GCP)).valider()
                }
            }
        }
        When("ukjent system lokalt") {
            Then("kastes ikke exception") {
                every { token.systemNavn } returns "ukjent-system"
                shouldNotThrowAny {
                    validator(listOf(LOCAL)).valider()
                }
            }
        }
    }
})
