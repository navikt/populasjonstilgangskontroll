package no.nav.tilgangsmaskin.ansatt.skjerming

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkel
import org.springframework.stereotype.Component

@Component
class SkjermingCacheOppfrisker(private val skjerming: SkjermingTjeneste) : AbstractCacheOppfrisker() {

    override val cacheName = SKJERMING
    @Timed
    override fun doOppfrisk(nøkkel: CacheNøkkel) =
        skjerming.skjerming(BrukerId(nøkkel.id))
}