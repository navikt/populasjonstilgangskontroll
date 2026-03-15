package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode
import no.nav.tilgangsmaskin.tilgang.Token

class AvdødTellerTest : DescribeSpec({

    val token = mockk<Token>().also {
        every { it.system } returns "test-system"
        every { it.clusterAndSystem } returns "test-system:dev-gcp"
    }

    describe("tell") {

        Dødsperiode.entries.forEach { intervall ->
            it("bruker intervall.tekst som months-tag for ${intervall.tekst}") {
                val registry = SimpleMeterRegistry()
                AvdødTeller(registry, token).tell(intervall, "NAV Testkontor")

                registry.find("dead.oppslag.total")
                    .tags("months", intervall.tekst, "enhet", "NAV Testkontor")
                    .counter()
                    .shouldNotBeNull()
                    .count() shouldBeExactly 1.0
            }
        }

        it("bruker enhet-argumentet som enhet-tag") {
            val registry = SimpleMeterRegistry()
            AvdødTeller(registry, token).tell(Dødsperiode.MND_0_6, "NAV Bergen")

            registry.find("dead.oppslag.total")
                .tag("enhet", "NAV Bergen")
                .counter()
                .shouldNotBeNull()
        }
    }
})
