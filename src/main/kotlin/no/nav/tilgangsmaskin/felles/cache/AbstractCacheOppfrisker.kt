package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import org.slf4j.LoggerFactory.getLogger
abstract class AbstractCacheOppfrisker(private val onFailure: (Throwable) -> Unit = {}) : CacheOppfrisker {
    private val log = getLogger(javaClass)

    protected abstract fun doOppfrisk(nøkkel: CacheNøkkel)

    @WithSpan
    final override fun oppfrisk(nøkkel: CacheNøkkel) {
        runCatching { doOppfrisk(nøkkel) }
            .onSuccess { success(nøkkel) }
            .onFailure { failure(nøkkel, it) }
    }

    protected fun success(nøkkel: CacheNøkkel) =
        log.trace("Oppfrisking av ${nøkkel.masked} i cache $cacheName OK")

    protected fun failure(nøkkel: CacheNøkkel, throwable: Throwable) {
        log.warn("Oppfrisking av ${nøkkel.masked} i cache $cacheName feilet", throwable)
        onFailure(throwable)
    }
}

