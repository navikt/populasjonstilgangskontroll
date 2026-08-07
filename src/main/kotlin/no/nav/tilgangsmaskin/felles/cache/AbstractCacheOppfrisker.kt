package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.observation.annotation.Observed
import org.slf4j.LoggerFactory.getLogger

abstract class AbstractCacheOppfrisker : CacheOppfrisker {
    private val log = getLogger(javaClass)

    protected abstract fun doOppfrisk(nøkkel: CacheNøkkel): Any?

    @Observed
    override fun oppfrisk(nøkkel: CacheNøkkel) =
        runCatching {
            doOppfrisk(nøkkel)
        }.onSuccess {
            log.trace("Oppfrisking av cache innslag ${nøkkel.maskert} OKOK")
        }.onFailure {
            log.warn("Oppfrisking av cache innslag ${nøkkel.maskert} feilet", it)
        }
}

