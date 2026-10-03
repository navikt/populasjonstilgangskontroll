package no.nav.tilgangsmaskin.ansatt.skjerming

import io.micrometer.core.annotation.Timed
import no.nav.sikkerhetstjenesten.felles.cache.AbstractCacheOppfrisker
import no.nav.sikkerhetstjenesten.felles.cache.CacheNøkkel
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.stereotype.Component

@Component
class SkjermingCacheOppfrisker(private val skjerming: SkjermingTjeneste) : AbstractCacheOppfrisker() {

    override val cacheName = SKJERMING
    @Timed
    override fun doOppfrisk(nøkkelElementer: CacheNøkkel) {
        skjerming.skjerming(BrukerId(nøkkelElementer.id))
    }
}