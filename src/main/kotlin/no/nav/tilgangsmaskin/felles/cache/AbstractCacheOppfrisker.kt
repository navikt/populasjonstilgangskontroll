package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import org.slf4j.LoggerFactory.getLogger
import org.springframework.util.StopWatch
abstract class AbstractCacheOppfrisker : CacheOppfrisker {
    protected val log = getLogger(javaClass)

    protected abstract fun doOppfrisk(elementer: CacheNøkkelElementer)

    @WithSpan
    final override fun oppfrisk(elementer: CacheNøkkelElementer) {
        val stopWatch = StopWatch().apply { start() }
        runCatching { doOppfrisk(elementer) }
            .onSuccess { suksess(elementer, stopWatch) }
            .onFailure { feil(elementer, it) }
    }
    
    private fun feil(elementer: CacheNøkkelElementer, e: Throwable) =
        log.warn("Oppfrisking av ${elementer.masked} feilet", e)

    private fun suksess(elementer: CacheNøkkelElementer, stopWatch: StopWatch) =
        log.info("Oppfrisking av ${elementer.masked} tok ${stopWatch.totalTimeMillis}ms")
}

