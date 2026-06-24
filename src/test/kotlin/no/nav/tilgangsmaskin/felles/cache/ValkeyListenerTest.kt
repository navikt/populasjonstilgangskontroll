package no.nav.tilgangsmaskin.felles.cache

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import kotlin.text.Charsets.UTF_8

class ValkeyListenerTest : BehaviorSpec({

    fun oppfrisker(navn: String) = mockk<CacheOppfrisker>().also {
        io.mockk.every { it.cacheName } returns navn
        io.mockk.every { it.oppfrisk(any()) } returns Unit
    }

    val nokkel = "pdl::medFamilie:03508331575"

    Given("et expired valkey-event") {

        When("lytteren er leder og cache-navnet matcher") {
            Then("kaller oppfrisk på riktig oppfrisker") {
                val oppfrisker = oppfrisker("pdl")
                val listener = ValkeyEventListeningCacheOppfrisker(true, oppfrisker)

                listener.onEvent(nokkel.toByteArray(UTF_8))

                verify {
                    oppfrisker.oppfrisk(CacheNøkkel(nokkel))
                }
            }
        }

        When("lytteren ikke er leder") {
            Then("kaller ikke oppfrisk") {
                val oppfrisker = oppfrisker("pdl")
                val listener = ValkeyEventListeningCacheOppfrisker(false, oppfrisker)

                listener.onEvent(nokkel.toByteArray(UTF_8))

                verify(exactly = 0) {
                    oppfrisker.oppfrisk(any())
                }
            }
        }

        When("ingen oppfrisker matcher cache-navnet") {
            Then("kaller ikke oppfrisk") {
                val oppfrisker = oppfrisker("annen-cache")
                val listener = ValkeyEventListeningCacheOppfrisker(true, oppfrisker)

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
                val listener = ValkeyEventListeningCacheOppfrisker(true, forste, andre)

                listener.onEvent(nokkel.toByteArray(UTF_8))

                verify { forste.oppfrisk(any()) }
                verify(exactly = 0) {
                    andre.oppfrisk(any())
                }
            }
        }

        // Telling verifiseres i AbstractCacheOppfriskerTest etter flytting av teller.
    }
})
