package no.nav.tilgangsmaskin.felles.utils

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.AbstractAuditor
import no.nav.tilgangsmaskin.felles.LocalAuditor
import no.nav.tilgangsmaskin.felles.SecureAuditor
import org.slf4j.Logger

class AuditorTest : BehaviorSpec({

    Given("LocalAuditor") {

        When("info kalles med melding") {
            Then("logger melding via klassens logger") {
                val logger = mockk<Logger>(relaxed = true)
                LocalAuditor(logger).info("test melding")
                verify { logger.info("test melding", null) }
            }
        }

        When("info kalles med melding og throwable") {
            Then("logger melding med throwable") {
                val logger = mockk<Logger>(relaxed = true)
                val throwable = RuntimeException("feil")
                LocalAuditor(logger).info("test melding", throwable)
                verify { logger.info("test melding", throwable) }
            }
        }
    }

    Given("SecureAuditor") {

        When("info kalles med sensitiv melding") {
            Then("logger melding via secureLog-loggeren") {
                val logger = mockk<Logger>(relaxed = true)
                SecureAuditor(logger).info("sensitiv melding")
                verify { logger.info("sensitiv melding", null) }
            }
        }

        When("info kalles med sensitiv melding og throwable") {
            Then("logger melding med throwable") {
                val logger = mockk<Logger>(relaxed = true)
                val throwable = RuntimeException("feil")
                SecureAuditor(logger).info("sensitiv melding", throwable)
                verify { logger.info("sensitiv melding", throwable) }
            }
        }
    }
})
