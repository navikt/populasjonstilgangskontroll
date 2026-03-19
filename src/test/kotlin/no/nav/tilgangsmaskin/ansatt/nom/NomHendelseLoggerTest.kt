package no.nav.tilgangsmaskin.ansatt.nom

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldNotBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class NomHendelseLoggerTest : DescribeSpec({

    val repo = mockk<NomRepository>()
    lateinit var registry: SimpleMeterRegistry
    lateinit var logger: NomHendelseLogger

    beforeEach {
        registry = SimpleMeterRegistry()
        every { repo.count() } returns 3L
        logger = NomHendelseLogger(registry, repo)
    }

    fun hendelse() = NomHendelse("08526835671", "Z999999", LocalDate.now(), null)

    describe("init") {
        it("registrerer nom.size gauge") {
            registry.find("nom.size").gauge() shouldNotBe null
        }

        it("gauge-verdi reflekterer repo.count()") {
            registry.find("nom.size").gauge()!!.value() shouldBeExactly 3.0
        }

        it("gauge-verdi oppdateres når repo.count() endres") {
            every { repo.count() } returns 7L
            registry.find("nom.size").gauge()!!.value() shouldBeExactly 7.0
        }
    }

    describe("ferdig") {
        it("kaller size() og re-registrerer gauge etter ferdigbehandling") {
            every { repo.count() } returns 5L
            shouldNotThrowAny { logger.ferdig(listOf(hendelse())) }
            registry.find("nom.size").gauge()!!.value() shouldBeExactly 5.0
        }
    }
})

