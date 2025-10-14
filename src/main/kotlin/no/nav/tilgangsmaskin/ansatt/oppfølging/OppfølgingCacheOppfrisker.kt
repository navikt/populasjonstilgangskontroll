package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelHandler.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import org.springframework.stereotype.Component

@Component
class OppfølgingCacheOppfrisker(private val oppfølging: OppfølgingTjeneste) : AbstractCacheOppfrisker() {

    override val cacheName: String = OPPFØLGING

    override fun doOppfrisk(nøkkelElementer: CacheNøkkelElementer) {
        oppfølging.enhetFor(nøkkelElementer.id)
    }
}