package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import org.slf4j.LoggerFactory.getLogger
abstract class AbstractCacheOppfrisker : CacheOppfrisker {
    private val log = getLogger(javaClass)

    protected abstract fun doOppfrisk(elementer: CacheNøkkelElementer)

    @WithSpan
    final override fun oppfrisk(elementer: CacheNøkkelElementer) {
        runCatching { doOppfrisk(elementer) }
            .onSuccess { log.info("Oppfrisking av ${elementer.masked} OK") }
            .onFailure { log.warn("Oppfrisking av ${elementer.masked} feilet", it) }
    }
}

