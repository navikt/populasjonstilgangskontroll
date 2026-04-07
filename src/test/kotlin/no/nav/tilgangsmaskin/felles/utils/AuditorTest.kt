package no.nav.tilgangsmaskin.felles.utils

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.mockk
import io.mockk.verify
import org.slf4j.Logger

class AuditorTest : DescribeSpec({

    fun <T : AbstractAuditor> T.withLogger(logger: Logger) = also {
        AbstractAuditor::class.java.getDeclaredField("logger").apply {
            isAccessible = true
            set(it, logger)
        }
    }

    describe("LocalAuditor") {

        it("bruker klassens enkle navn som loggernavn") {
            val logger = mockk<Logger>(relaxed = true)
            LocalAuditor().withLogger(logger).info("test melding")

            verify { logger.info("test melding", null) }
        }

        it("logger melding med throwable") {
            val logger = mockk<Logger>(relaxed = true)
            val throwable = RuntimeException("feil")
            LocalAuditor().withLogger(logger).info("test melding", throwable)

            verify { logger.info("test melding", throwable) }
        }
    }

    describe("SecureAuditor") {

        it("bruker 'secureLog' som loggernavn") {
            val logger = mockk<Logger>(relaxed = true)
            SecureAuditor().withLogger(logger).info("sensitiv melding")

            verify { logger.info("sensitiv melding", null) }
        }

        it("logger melding med throwable") {
            val logger = mockk<Logger>(relaxed = true)
            val throwable = RuntimeException("feil")
            SecureAuditor().withLogger(logger).info("sensitiv melding", throwable)

            verify { logger.info("sensitiv melding", throwable) }
        }
    }
})
