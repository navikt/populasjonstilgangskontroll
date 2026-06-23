package no.nav.tilgangsmaskin.felles.cache

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.doubles.shouldBeExactly
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.tilgang.Token
import kotlin.text.Charsets.UTF_8

class ValkeyListenerTest : BehaviorSpec({

    val token = mockk<Token>().also {
        every { it.system } returns "test"
        every { it.clusterAndSystem } returns "test:dev-gcp"
    }

    fun teller() = CacheOppfriskerTeller(SimpleMeterRegistry(), token)

    fun oppfrisker(navn: String) = mockk<CacheOppfrisker>().also {
        every { it.cacheName } returns navn
        every { it.oppfrisk(any()) } returns Unit
    }

    val nokkel = "pdl::medFamilie:03508331575"

    Given("et expired valkey-event") {

        When("lytteren er leder og cache-navnet matcher") {
            Then("kaller oppfrisk på riktig oppfrisker") {
                val oppfrisker = oppfrisker("pdl")
                val listener = ValkeyListener(teller(), true, oppfrisker)

                listener.onEvent(nokkel.toByteArray(UTF_8))

                verify {
                    oppfrisker.oppfrisk(CacheNøkkel(nokkel))
                }
            }
        }

        When("lytteren ikke er leder") {
            Then("kaller ikke oppfrisk") {
                val oppfrisker = oppfrisker("pdl")
                val listener = ValkeyListener(teller(), false, oppfrisker)

                listener.onEvent(nokkel.toByteArray(UTF_8))

                verify(exactly = 0) {
                    oppfrisker.oppfrisk(any())
                }
            }
        }

        When("ingen oppfrisker matcher cache-navnet") {
            Then("kaller ikke oppfrisk") {
                val oppfrisker = oppfrisker("annen-cache")
                val listener = ValkeyListener(teller(), true, oppfrisker)

                listener.onEvent(nokkel.toByteArray(UTF_8))

                verify(exactly = 0) {
                    oppfrisker.oppfrisk(any())
                }
            }
        }

        When("flere oppfriskere er registrert for samme cache") {
            Then("bruker bare første matchende oppfrisker") {
                val forste = oppfrisker("pdl")
                val andre = oppfrisker("pdl")
                val listener = ValkeyListener(teller(), true, forste, andre)

                listener.onEvent(nokkel.toByteArray(UTF_8))

                verify { forste.oppfrisk(any()) }
                verify(exactly = 0) {
                    andre.oppfrisk(any())
                }
            }
        }

        When("vellykket oppfrisking") {
            Then("oppdaterer teller med cache-navn og metode") {
                val registry = SimpleMeterRegistry()
                val oppfrisker = oppfrisker("pdl")
                val listener = ValkeyListener(CacheOppfriskerTeller(registry, token), true, oppfrisker)

                listener.onEvent(nokkel.toByteArray(UTF_8))

                registry.find("cache.oppfrisker")
                    .tags("cache", "pdl", "result", "expired", "method", "medFamilie")
                    .counter()!!
                    .count() shouldBeExactly 1.0
            }
        }
    }
})
