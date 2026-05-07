package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import org.slf4j.LoggerFactory.getLogger
abstract class AbstractCacheOppfrisker : CacheOppfrisker {
    private val log = getLogger(javaClass)

    protected abstract fun doOppfrisk(nøkkel: CacheNøkkel)

    @WithSpan
    final override fun oppfrisk(nøkkel: CacheNøkkel) {
        runCatching { doOppfrisk(nøkkel) }
            .onSuccess { log.info("Oppfrisking av cache innslag ${nøkkel.masked} OK") }
            .onFailure { log.warn("Oppfrisking av cache innslag ${nøkkel.masked} feilet", it) }
    }
}

