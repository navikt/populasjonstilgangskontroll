package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheOppfrisker
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelElementer
import org.springframework.stereotype.Component

@Component
class OppfølgingCacheOppfrisker(private val oppfølging: OppfølgingTjeneste) : AbstractCacheOppfrisker() {

    override val cacheName = OPPFØLGING

    override fun doOppfrisk(elementer: CacheNøkkelElementer) {
        oppfølging.enhetFor(Identifikator(elementer.id))
    }
}