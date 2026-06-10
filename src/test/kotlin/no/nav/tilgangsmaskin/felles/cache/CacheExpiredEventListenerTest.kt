package no.nav.tilgangsmaskin.felles.cache

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.doubles.shouldBeExactly
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.cache.CacheElementUtløptLytter.CacheInnslagFjernetHendelse
import no.nav.tilgangsmaskin.regler.motor.Tellere
import no.nav.tilgangsmaskin.tilgang.Token

class CacheExpiredEventListenerTest : BehaviorSpec({

    val token = mockk<Token>().also {
        every { it.system } returns "test"
        every { it.clusterAndSystem } returns "test:dev-gcp"
    }

    fun tellere() = Tellere(SimpleMeterRegistry(), token)

    fun oppfrisker(navn: String) = mockk<CacheOppfrisker>().also {
        every { it.cacheName } returns navn
        every { it.oppfrisk(any()) } returns Unit
    }

    val nkkel = "pdl::medFamilie:03508331575"
    val hendelse = CacheInnslagFjernetHendelse(nkkel)

    Given("cacheInnslagFjernet kalles") {

        When("lytteren er startet") {
            Then("kaller oppfrisk på riktig oppfrisker") {
                val oppfrisker = oppfrisker("pdl")
                val listener = CacheExpiredEventListener(tellere(), true, oppfrisker)
                listener.start()
                listener.cacheInnslagFjernet(hendelse)
                verify {
                    oppfrisker.oppfrisk(CacheNøkkel(nkkel))
                }
            }
        }

        When("lytteren ikke er startet") {
            Then("kaller ikke oppfrisk") {
                val oppfrisker = oppfrisker("pdl")
                val listener = CacheExpiredEventListener(tellere(), true, oppfrisker)
                listener.cacheInnslagFjernet(hendelse)
                verify(exactly = 0) {
                    oppfrisker.oppfrisk(any())
                }
            }
        }

        When("lytteren er stoppet") {
            Then("kaller ikke oppfrisk") {
                val oppfrisker = oppfrisker("pdl")
                val listener = CacheExpiredEventListener(tellere(), erLeder = true, oppfrisker)
                listener.start()
                listener.stop()
                listener.cacheInnslagFjernet(hendelse)
                verify(exactly = 0) {
                    oppfrisker.oppfrisk(any())
                }
            }
        }

        When("instansen ikke er leder") {
            Then("kaller ikke oppfrisk") {
                val oppfrisker = oppfrisker("pdl")
                val listener = CacheExpiredEventListener(tellere(), false, oppfrisker)
                listener.start()
                listener.cacheInnslagFjernet(hendelse)
                verify(exactly = 0) {
                    oppfrisker.oppfrisk(any())
                }
            }
        }

        When("ingen oppfrisker matcher cache-navnet") {
            Then("kaller ikke oppfrisk") {
                val oppfrisker = oppfrisker("annen-cache")
                val listener = CacheExpiredEventListener(tellere(), erLeder = true, oppfrisker)
                listener.start()
                listener.cacheInnslagFjernet(hendelse)
                verify(exactly = 0) {
                    oppfrisker.oppfrisk(any())
                }
            }
        }

        When("flere oppfriskere er registrert for samme cache") {
            Then("bruker bare første matchende oppfrisker") {
                val første = oppfrisker("pdl")
                val andre = oppfrisker("pdl")
                val listener = CacheExpiredEventListener(tellere(), erLeder = true, første, andre)
                listener.start()
                listener.cacheInnslagFjernet(hendelse)
                verify { første.oppfrisk(any()) }
                verify(exactly = 0) {
                    andre.oppfrisk(any())
                }
            }
        }

        When("vellykket oppfrisking") {
            Then("oppdaterer teller med cache-navn og metode") {
                val registry = SimpleMeterRegistry()
                val oppfrisker = oppfrisker("pdl")
                val listener = CacheExpiredEventListener(Tellere(registry, token), erLeder = true, oppfrisker)
                listener.start()
                listener.cacheInnslagFjernet(hendelse)
                registry.find("cache.oppfrisker")
                    .tags("cache", "pdl", "result", "expired", "method", "medFamilie")
                    .counter()!!
                    .count() shouldBeExactly 1.0
            }
        }
    }
})

