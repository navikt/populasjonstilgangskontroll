package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelHandler.CacheNøkkelElementer
import org.slf4j.LoggerFactory.getLogger

abstract class AbstractCacheOppfrisker : CacheOppfrisker {
    private val log = getLogger(javaClass)

    final override fun oppfrisk(nøkkelElementer: CacheNøkkelElementer) {
        runCatching {
            doOppfrisk(nøkkelElementer)
            log.info("Oppfrisking av ${nøkkelElementer.nøkkel} OK")
        }.getOrElse {
            log.info("Oppfrisking av ${nøkkelElementer.nøkkel} etter sletting feilet, dette er ikke kritisk", it)
        }
    }

    protected abstract fun doOppfrisk(nøkkelElementer: CacheNøkkelElementer)
}

