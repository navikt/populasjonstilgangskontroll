package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import kotlin.system.measureTimeMillis

abstract class AbstractCacheOppfrisker : CacheOppfrisker {
    protected val log = getLogger(javaClass)

    protected abstract fun doOppfrisk(elementer: CacheNøkkelElementer)

    @WithSpan
    final override fun oppfrisk(elementer: CacheNøkkelElementer) {
        val varighet = measureTimeMillis {
            runCatching {
                doOppfrisk(elementer)
                log.info("Oppfrisking av ${elementer.cacheName}::${elementer.id.maskFnr()} OK")
            }.getOrElse {
                oppfriskingFeilet(elementer, it)
            }
        }
        log.info("Oppfrisking tok ${varighet}ms for ${elementer.cacheName}::${elementer.id.maskFnr()}")
    }
  protected fun oppfriskingFeilet(elementer: CacheNøkkelElementer, e: Throwable) {
        log.warn("Oppfrisking av ${elementer.cacheName}::${elementer.id.maskFnr()} feilet", e)
    }
}

