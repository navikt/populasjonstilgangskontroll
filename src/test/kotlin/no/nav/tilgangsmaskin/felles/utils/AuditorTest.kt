package no.nav.tilgangsmaskin.felles.utils

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.rest.notifikasjon.LocalAuditor
import no.nav.tilgangsmaskin.felles.rest.notifikasjon.SecureAuditor
import org.slf4j.Logger

class AuditorTest : BehaviorSpec({
    val logger = mockk<Logger>(relaxed = true)

    Given("LocalAuditor") {
        val auditor = LocalAuditor(logger)
        beforeEach {
            clearMocks(logger)
        }

        When("info kalles med melding") {
            Then("logger melding via klassens logger") {
                auditor.info("test melding")
                verify { logger.info("test melding") }
            }
        }

        When("info kalles med melding og throwable") {
            Then("logger melding med throwable") {
                val throwable = RuntimeException("feil")
                auditor.info("test melding", throwable)
                verify { logger.info("test melding", throwable) }
            }
        }
    }

    Given("SecureAuditor") {
        val auditor = SecureAuditor("secureLog")

        When("info kalles med sensitiv melding") {
            Then("logger melding via secureLog-loggeren uten å feile") {
                shouldNotThrowAny { auditor.info("sensitiv melding") }
            }
        }

        When("info kalles med sensitiv melding og throwable") {
            Then("logger melding med throwable uten å feile") {
                shouldNotThrowAny {
                    auditor.info("sensitiv melding", RuntimeException("feil"))
                }
            }
        }
    }
})
