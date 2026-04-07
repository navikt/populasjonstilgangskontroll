package no.nav.tilgangsmaskin.ansatt.nom

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldNotBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class NomHendelseLoggerTest : BehaviorSpec({

    val repo = mockk<NomRepository>()
    lateinit var registry: SimpleMeterRegistry
    lateinit var logger: NomHendelseLogger

    fun hendelse() = NomHendelse("08526835671", "Z999999", LocalDate.now(), null)

    beforeEach {
        registry = SimpleMeterRegistry()
        every { repo.count() } returns 3L
        logger = NomHendelseLogger(registry, repo)
    }

    Given("NomHendelseLogger er initialisert") {
        Then("nom.size gauge er registrert") {
            registry.find("nom.size").gauge() shouldNotBe null
        }

        Then("gauge-verdi reflekterer repo.count()") {
            registry.find("nom.size").gauge()!!.value() shouldBeExactly 3.0
        }

        Then("gauge-verdi oppdateres når repo.count() endres") {
            every { repo.count() } returns 7L
            registry.find("nom.size").gauge()!!.value() shouldBeExactly 7.0
        }
    }

    Given("ferdig kalles med en liste hendelser") {
        When("repo.count() returnerer 5") {
            Then("gauge-verdien oppdateres til 5") {
                every { repo.count() } returns 5L
                shouldNotThrowAny { logger.ferdig(listOf(hendelse())) }
                registry.find("nom.size").gauge()!!.value() shouldBeExactly 5.0
            }
        }
    }
})
