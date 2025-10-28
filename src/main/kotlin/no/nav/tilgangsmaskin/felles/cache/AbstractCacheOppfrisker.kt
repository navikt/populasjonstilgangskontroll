package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger

abstract class AbstractCacheOppfrisker : CacheOppfrisker {
    private val log = getLogger(javaClass)

    final override fun oppfrisk(nøkkelElementer: CacheNøkkelElementer) {
        runCatching {
            doOppfrisk(nøkkelElementer)
            log.info("Oppfrisking av ${nøkkelElementer.cacheName}::${nøkkelElementer.id.maskFnr()} OK")
        }.getOrElse {
            log.info("Oppfrisking av ${nøkkelElementer.nøkkel} etter sletting feilet, dette er ikke kritisk", it)
        }
    }

    protected abstract fun doOppfrisk(nøkkelElementer: CacheNøkkelElementer)
}

interface CacheOppfrisker {
    val cacheName: String
    fun oppfrisk(nøkkelElementer: CacheNøkkelElementer)
}

data class CacheNøkkelElementer(val nøkkel: String) {
    private val elementer = nøkkel.split("::", ":")
    val cacheName = elementer.first()
    val metode = if (elementer.size > 2) elementer[1] else null
    val id = elementer.last()
}