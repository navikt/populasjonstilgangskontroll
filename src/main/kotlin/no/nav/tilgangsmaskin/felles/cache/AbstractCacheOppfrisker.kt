package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.annotation.Timed
import io.opentelemetry.instrumentation.annotations.WithSpan
import org.slf4j.LoggerFactory.getLogger

abstract class AbstractCacheOppfrisker : CacheOppfrisker {
    private val log = getLogger(javaClass)

    protected abstract fun doOppfrisk(nøkkel: CacheNøkkel): Any?

    @WithSpan
    @Timed
    final override fun oppfrisk(nøkkel: CacheNøkkel) =
        runCatching {
            doOppfrisk(nøkkel)
        }.onSuccess {
            log.trace("Oppfrisking av cache innslag ${nøkkel.maskert} OK")
        }.onFailure {
            log.warn("Oppfrisking av cache innslag ${nøkkel.maskert} feilet", it)
        }
}

