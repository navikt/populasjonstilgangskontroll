package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import org.slf4j.LoggerFactory.getLogger
abstract class AbstractCacheOppfrisker : CacheOppfrisker {
    protected val log = getLogger(javaClass)

    protected abstract fun doOppfrisk(elementer: CacheNøkkelElementer)

    @WithSpan
    final override fun oppfrisk(elementer: CacheNøkkelElementer) {
        runCatching { doOppfrisk(elementer) }
            .onSuccess { suksess(elementer) }
            .onFailure { feil(elementer, it) }
    }

    protected fun feil(elementer: CacheNøkkelElementer, e: Throwable) =
        log.warn("Oppfrisking av ${elementer.masked} feilet", e)

    private fun suksess(elementer: CacheNøkkelElementer,) =
        log.info("Oppfrisking av ${elementer.masked} OK")
}

