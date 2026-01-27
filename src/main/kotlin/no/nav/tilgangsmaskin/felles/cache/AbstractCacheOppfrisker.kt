package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.mask
import org.slf4j.LoggerFactory.getLogger
import kotlin.system.measureTimeMillis

abstract class AbstractCacheOppfrisker : CacheOppfrisker {
    protected val log = getLogger(javaClass)

    protected abstract fun doOppfrisk(elementer: CacheNøkkelElementer)

    @WithSpan
    final override fun oppfrisk(elementer: CacheNøkkelElementer) {
        val duration = measureTimeMillis {
            runCatching {
                doOppfrisk(elementer)
                log.info("Oppfrisking av ${elementer.cacheName}::${elementer.id.mask()} OK")
            }.getOrElse {
                loggOppfriskingFeilet(elementer, it)
            }
        }
        log.info("Oppfrisking tok ${duration}ms for ${elementer.cacheName}::${elementer.id.mask()}")
    }
  protected fun loggOppfriskingFeilet(elementer: CacheNøkkelElementer, feil: Throwable) {
        log.warn("Oppfrisking av ${elementer.cacheName}::${elementer.id.mask()} feilet", feil)
    }
}

interface CacheOppfrisker {
    val cacheName: String
    fun oppfrisk(elementer: CacheNøkkelElementer)
}

data class CacheNøkkelElementer(val nøkkel: String) {
    private val elementer = nøkkel.split("::", ":")
    val cacheName = elementer.first()
    val metode = if (elementer.size > 2) elementer[1] else null
    val id = elementer.last()
}