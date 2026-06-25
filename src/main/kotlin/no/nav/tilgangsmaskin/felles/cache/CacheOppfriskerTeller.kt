package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.Timer
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.NANOSECONDS
import kotlin.time.Duration

@Component
class CacheOppfriskerTeller(private val registry: MeterRegistry, token: Token) :
    AbstractTeller(registry, token, "cache.oppfrisker", "Antall oppfriskninger av cache etter utløp") {

    fun tellTid(cacheName: String, varighet: Duration) =
        Timer.builder("cache.oppfrisker.varighet")
            .description("Varighet av cache-oppfrisking etter Redis-utløp")
            .tag("cache", cacheName)
            .register(registry)
            .record(varighet.inWholeNanoseconds, NANOSECONDS)
}

