package no.nav.tilgangsmaskin.felles.cache

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.shouldBeExactly
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.cache.CacheElementUtløptLytter.CacheInnslagFjernetEvent
import no.nav.tilgangsmaskin.regler.motor.CacheOppfriskerTeller
import no.nav.tilgangsmaskin.tilgang.Token
import io.mockk.every

class CacheExpiredEventListenerTest : DescribeSpec({

    val token = mockk<Token>().also {
        every { it.system } returns "test"
        every { it.clusterAndSystem } returns "test:dev-gcp"
    }

    fun teller() = CacheOppfriskerTeller(SimpleMeterRegistry(), token)

    fun oppfrisker(navn: String) = mockk<CacheOppfrisker>().also {
        every { it.cacheName } returns navn
        every { it.oppfrisk(any()) } returns Unit
    }

    // nøkkel format: "cacheName::metode:id" or "cacheName::id"
    val nøkkel = "pdl::medFamilie:03508331575"
    val hendelse = CacheInnslagFjernetEvent(nøkkel)

    describe("cacheInnslagFjernet") {

        it("kaller oppfrisk på riktig oppfrisker når lytteren kjører") {
            val oppfrisker = oppfrisker("pdl")
            val listener = CacheExpiredEventListener(teller(), true, oppfrisker)
            listener.start()
            listener.cacheInnslagFjernet(hendelse)
            verify { oppfrisker.oppfrisk(CacheNøkkelElementer(nøkkel)) }
        }

        it("kaller ikke oppfrisk når lytteren ikke er startet") {
            val oppfrisker = oppfrisker("pdl")
            val listener = CacheExpiredEventListener(teller(), true, oppfrisker)
            // start() not called

            listener.cacheInnslagFjernet(hendelse)

            verify(exactly = 0) { oppfrisker.oppfrisk(any()) }
        }

        it("kaller ikke oppfrisk etter stop()") {
            val oppfrisker = oppfrisker("pdl")
            val listener = CacheExpiredEventListener(teller(), erLeder = true, oppfrisker)
            listener.start()
            listener.stop()

            listener.cacheInnslagFjernet(hendelse)

            verify(exactly = 0) { oppfrisker.oppfrisk(any()) }
        }

        it("kaller ikke oppfrisk når instansen ikke er leder") {
            val oppfrisker = oppfrisker("pdl")
            val listener = CacheExpiredEventListener(teller(), false, oppfrisker)
            listener.start()

            listener.cacheInnslagFjernet(hendelse)

            verify(exactly = 0) { oppfrisker.oppfrisk(any()) }
        }

        it("kaller ikke oppfrisk når ingen oppfrisker matcher cache-navnet") {
            val oppfrisker = oppfrisker("annen-cache")
            val listener = CacheExpiredEventListener(teller(), erLeder = true, oppfrisker)
            listener.start()

            listener.cacheInnslagFjernet(hendelse)

            verify(exactly = 0) { oppfrisker.oppfrisk(any()) }
        }

        it("bruker bare første matchende oppfrisker når flere er registrert") {
            val første = oppfrisker("pdl")
            val andre = oppfrisker("pdl")
            val listener = CacheExpiredEventListener(teller(), erLeder = true, første, andre)
            listener.start()

            listener.cacheInnslagFjernet(hendelse)

            verify{ første.oppfrisk(any()) }
            verify(exactly = 0) { andre.oppfrisk(any()) }
        }

        it("oppdaterer teller med cache-navn og metode etter vellykket oppfrisking") {
            val registry = SimpleMeterRegistry()
            val oppfrisker = oppfrisker("pdl")
            val listener = CacheExpiredEventListener(CacheOppfriskerTeller(registry, token), erLeder = true, oppfrisker)
            listener.start()

            listener.cacheInnslagFjernet(hendelse)

            registry.find("cache.oppfrisker")
                .tags("cache", "pdl", "result", "expired", "method", "medFamilie")
                .counter()!!
                .count() shouldBeExactly 1.0
        }
    }
})

